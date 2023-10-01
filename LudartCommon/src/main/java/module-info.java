module fr.onsiea.ludart.common
{
	requires fr.onsiea.tools.logger;
	requires fr.onsiea.tools.utils;
	requires transitive lombok;

	exports fr.onsiea.ludart.common.modules;
	exports fr.onsiea.ludart.common.modules.nodes;
	exports fr.onsiea.ludart.common.modules.schema;
	exports fr.onsiea.ludart.common.modules.schema.instanced;
	exports fr.onsiea.ludart.common.modules.settings;
	exports fr.onsiea.ludart.common.modules.stack;
	exports fr.onsiea.ludart.common.registries;
	exports fr.onsiea.ludart.common.registries.manager;
	exports fr.onsiea.ludart.common.registry;
	exports fr.onsiea.ludart.common.modules.processor;
}