/**
 * @author wpalmer
 * 
 */
package uk.bl.dpt.tika2fits;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.SAXException;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.oisfileinfo.FileInfo;
import edu.harvard.hul.ois.fits.tools.tika.MetadataFormatter;
import edu.harvard.hul.ois.fits.tools.tika.TikaTool;


/**
 * Convenience code for converting Tika Metdata to FITS output format
 * @author wpalmer
 */
public class Tika2Fits {

	private static Parser gParser;
	private static TikaTool gTikaTool;
	private static FileInfo gFileInfo;

	final private static Namespace ns = Namespace.getNamespace("fits", Fits.XML_NAMESPACE);

	private static void init() {
		gParser = new AutoDetectParser();
		gTikaTool = new TikaTool();
		gFileInfo = new FileInfo();
	}
	
	@SuppressWarnings("unused")
	private static void print(final Metadata pMetadata, final Document pDocument, final String pMDXML) {

		for(String k:pMetadata.names()) {
			for(String v:pMetadata.getValues(k)) {
				System.out.println(k+": "+v);
			}
		}
		
		final String xml = new XMLOutputter().outputString(pDocument);
		
		System.out.println(xml);
		
		System.out.println(pMDXML);		
	}
	
	private static Metadata parse(File pFile) {
		Metadata metadata = new Metadata();
		
		try {
			InputStream is = new FileInputStream(pFile);
			gParser.parse(is, new NullContentHandler(), metadata, new ParseContext());
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (SAXException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (TikaException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		return metadata;
	}

	private static void extractNormalisedMetadata(File pFile, File pOutput) {
		saveXML(process(parse(pFile), pFile), pOutput);
	}
	
	private static Document process(Metadata pMetadata, File pFile) {
		
		// Normalise the Tika Metadata Object to FITS XML
		Document normalisedTika;
		try {
			normalisedTika = gTikaTool.buildToolData(pMetadata);
		} catch (FitsToolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		
		Document fileInfo;
		try {
			fileInfo = gFileInfo.createXml(pFile);
		} catch (FitsToolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

		mergeXML(normalisedTika, fileInfo, "fileinfo");
		
		// raw metadata as xml
		// quick hack to add namespace so we can reuse mergeXML()
		final String rawXml = "<fits xmlns=\"http://hul.harvard.edu/ois/xml/ns/fits/fits_output\">"+MetadataFormatter.toXML(pMetadata)+"</fits>";

		Document md = null; 
		try {
			md = new SAXBuilder().build(new ByteArrayInputStream(rawXml.getBytes()));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mergeXML(normalisedTika, md, "metadata");

		return normalisedTika;
		
	}
	
	private static void saveXML(Document pDocument, File pOutputFile) {
		final String xml = new XMLOutputter().outputString(pDocument);
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(pOutputFile));
			pw.println(xml);
			pw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void mergeXML(Document pDoc1, Document pDoc2, String pElement) {
		List nodes = pDoc2.getRootElement().getChild(pElement, ns).getChildren();
		Element element = pDoc1.getRootElement().getChild(pElement, ns);
		for(int i=0;i<nodes.size();i++) {
			Element e = (Element)nodes.get(i);
			element.addContent(e.detach());
		}		
	}

	/**
	 * Test main method
	 * @param args
	 */
	public static void main(String[] args) {

		init();
		
		final String dir = "src/test/resources/";
		String file = "test.docx";
		extractNormalisedMetadata(new File(dir+file), new File(file+".fits.xml"));
		file = "test.bmp";
		extractNormalisedMetadata(new File(dir+file), new File(file+".fits.xml"));
		file = "test.pdf";
		extractNormalisedMetadata(new File(dir+file), new File(file+".fits.xml"));
		

	}

}
