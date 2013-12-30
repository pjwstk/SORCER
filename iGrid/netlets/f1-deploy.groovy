#!/usr/bin/env nsh -f

import static sorcer.eo.operator.*;
import static sorcer.core.deploy.Deployment.*;
import static sorcer.service.Strategy.*;
import junit.sorcer.core.provider.*;
import sorcer.service.*;
import sorcer.core.provider.*;
import sorcer.core.deploy.Deployment.*;
	
	
        Task f4 = task("f4",
                       sig("multiply",
                           Multiplier.class,
                           deploy(configuration("bin/sorcer/test/arithmetic/configs/multiplier-prv.config"),
                                  idle(1),
                                  Type.SELF)),
                       context("multiply", input("arg/x1", 10.0d),
                               input("arg/x2", 50.0d), out("result/y1", null)));

        Task f5 = task("f5",
                       sig("add",
                           Adder.class,
                           deploy(configuration("bin/sorcer/test/arithmetic/configs/AdderProviderConfig.groovy"))),
                       context("add", input("arg/x3", 20.0d), input("arg/x4", 80.0d),
                               output("result/y2", null)));

        Task f3 = task("f3",
                       sig("subtract", Subtractor.class,
                           deploy(maintain(2, perNode(2)),
                                  idle(1),
                                  configuration("bin/sorcer/test/arithmetic/configs/subtractor-prv.config"))),
                       context("subtract", input("arg/x5", null),
                               input("arg/x6", null), output("result/y3", null)));					   
						  
        job("f1", sig("service", Jobber.class, "Jobber", deploy(Unique.YES)),
                   job(sig("service", Jobber.class, "Jobber"), "f2", f4, f5), f3,
                   strategy(Provision.YES),
                   pipe(out(f4, "result/y1"), input(f3, "arg/x5")),
                   pipe(out(f5, "result/y2"), input(f3, "arg/x6")));
