package fr.onsiea.ludart.common.modules.schema;

import fr.onsiea.ludart.common.modules.IModule;
import fr.onsiea.tools.utils.function.IOFunction;
import lombok.Getter;

@Getter
public class ModuleSchema
{
	private final Class<?> sourceModuleClass;
	private final String              sourceModuleName;
	private final IOFunction<IModule> moduleInitializer;
	private final boolean             isNeeded;
	private final String[] dependencies;

	public ModuleSchema(Class<?> sourceModuleClassIn, IOFunction<IModule> moduleInitializerIn, boolean isNeededIn, Class<? extends IModule>... dependenciesIn)
	{
		this.sourceModuleClass = sourceModuleClassIn;
		this.sourceModuleName = sourceModuleClassIn.getSimpleName();
		this.moduleInitializer = moduleInitializerIn;
		this.isNeeded = isNeededIn;
		this.dependencies = new String[dependenciesIn.length];
		if (dependenciesIn == null)
		{
			return;
		}

		int i = 0;
		for (var dependency : dependenciesIn)
		{
			if (dependency != null)
			{
				this.dependencies[i] = dependency.getSimpleName();
			}

			i++;
		}
	}
}