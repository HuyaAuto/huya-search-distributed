package com.huya.search.inject;

import com.google.inject.Module;

/**
 * @author colin.ke keqinwu@yy.com
 */
public interface SpawnModule {

	Iterable<? extends Module> spawnModules();

}
