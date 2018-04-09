package com.huya.search.inject;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.huya.search.service.LifecycleService;
import com.huya.search.util.Pair;

/**
 * @author colin.ke keqinwu@yy.com
 */
public class ModulesBuilder implements Iterable<Module> {

	private static final class ModulesBuilderHandle {
		private static ModulesBuilder INSTANCE = new ModulesBuilder();
	}

	public static ModulesBuilder getInstance() {
		return ModulesBuilderHandle.INSTANCE;
	}

	private ModulesBuilder() {}

	private Injector injector;

	private final List<Module> modules = Lists.newArrayList();
	private final List<Class<? extends LifecycleService>> services = Lists.newArrayList();

	public ModulesBuilder add(Module... modules) {
		for (Module module : modules) {
			add(module);
		}
		return this;
	}

	public ModulesBuilder add(Collection<? extends Module> modules) {
		for (Module module : modules) {
			add(module);
		}
		return this;
	}

	public ModulesBuilder add(Module module) {
		modules.add(module);
		if (module instanceof SpawnModule) {
			Iterable<? extends Module> spawned = ((SpawnModule) module).spawnModules();
			for (Module spawn : spawned) {
				add(spawn);
			}
		}
		if(module instanceof ServiceModule) {
			services.addAll(((ServiceModule) module).services());
		}
		return this;
	}

	@Override
	public Iterator<Module> iterator() {
		return modules.iterator();
	}

	public synchronized Injector createInjector() {
		if (this.injector == null) {
			this.injector = Guice.createInjector(modules);
		}
		return this.injector;
	}

	public Pair<Injector, List<Class<? extends LifecycleService>>> createInjectorPair() {
		return new Pair<>(injector, services);
	}

	public Pair<Injector, List<Class<? extends LifecycleService>>> createChildInjectorPair(Injector injector) {
		return new Pair<>(injector, services);
	}
}
