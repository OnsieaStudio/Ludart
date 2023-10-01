package fr.onsiea.ludart.common.modules.schema.instanced;

import fr.onsiea.ludart.common.modules.schema.AtomicCounter;
import fr.onsiea.ludart.common.modules.schema.ModuleSchema;
import lombok.Getter;

@Getter
public class ModulePositionedSchema
{
	private final ModuleSchema schema;
	private final AtomicCounter position;

	public ModulePositionedSchema(ModuleSchema schemaIn, AtomicCounter previousPositionIn)
	{
		this.schema = schemaIn;
		this.position = new AtomicCounter(previousPositionIn.value() + 1, previousPositionIn);
	}

	public final boolean isAfter(AtomicCounter positionIn)
	{
		return this.position.value() > positionIn.value();
	}
}