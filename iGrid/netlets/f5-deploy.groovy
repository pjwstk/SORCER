#!/usr/bin/env nsh -f

import static sorcer.eo.operator.*;
import static sorcer.service.Strategy.*;
import junit.sorcer.core.provider.*;
import sorcer.service.*;
import sorcer.core.provider.*;
import sorcer.core.deploy.Deployment;
import sorcer.core.deploy.Deployment.*;


	task("f5",
		sig("add", Adder.class, deploy(configuration("bin/sorcer/test/arithmetic/configs/AdderProviderConfig.groovy"))),
			context("add", input("arg/x3", 20.0d), input("arg/x4", 80.0d), output("result/y2")),
			strategy(Provision.YES));
