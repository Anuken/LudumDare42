package io.anuke.ld42;

import io.anuke.ucore.modules.ModuleCore;

public class LD42 extends ModuleCore {
	
	@Override
	public void init(){
		module(Vars.control = new Control());
		module(Vars.ui = new UI());
	}
	
}
