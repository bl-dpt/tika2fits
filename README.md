Tika2Fits
=========

[![Build Status](https://travis-ci.org/willp-bl/tika2fits.png)](https://travis-ci.org/willp-bl/tika2fits)

Tika2Fits is an attempt to reuse the metadata normalisation code from [Fits](https://github.com/harvard-lts/fits), enabling Tika characterisation output to be used wherever Fits output can be consumed.  Tika2Fits can normalise Tika output into Fits-compatible output but the project is currently experimental and not at all tested!  Saying that, three test files have been loaded into [c3po](https://github.com/openplanets/c3po).

An aim is to leave as much of the Fits code unmodified as possible.

Status
------

* The code runs (and is barely tested!)
* The main class/API is in-progress and an API doesn't exist
* It is not currently suitable for production use 

License
-------

This repository incorporates code from Fits under its original license(s).  Any new code is currently under the LGPL (as Fits is [stated to be](http://projects.iq.harvard.edu/fits)). Please note that some Fits code may be licensed under the GPL or Apache2 licenses (for clarification see the Fits repository). 

Acknowledgements
----------------

This work was partially supported by the [SCAPE project](http://scape-project.eu/). The SCAPE project is co-funded by the European Union under FP7 ICT-2009.4.1 (Grant Agreement number 270137)
