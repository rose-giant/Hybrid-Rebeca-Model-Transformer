package org.rebecalang.modeltransformer;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.ExceptionContainer;

public abstract class AbstractModelTransformer {

	protected Set<String> getFeaturesNames(Set<?> features) {
		Set<String> featuresNames = new HashSet<String>();
		for (Object o : features) {
			featuresNames.add(((Enum<?>) o).name());
		}
		return featuresNames;
	}

	protected ExceptionContainer container;
	protected File destinationLocation;
	protected Set<CompilerExtension> compilerFeatures;
	protected Set<String> transformingFeaturesNames;
	protected Set<String> compilerFeaturesNames;
	protected RebecaModel rebecaModel;
	protected String modelName;

	public void prepare(String modelName, RebecaModel rebecaModel,
			Set<CompilerExtension> compilerFeatures,
			CommandLine commandLine, File destinationLocation,
			ExceptionContainer container) {
		this.rebecaModel = rebecaModel;
		this.compilerFeatures = compilerFeatures;
		this.destinationLocation = destinationLocation;
		this.container = container;
		this.modelName = modelName;
		this.compilerFeaturesNames = getFeaturesNames(compilerFeatures);

		if (destinationLocation != null)
			destinationLocation.mkdirs();
	}

	public abstract void transformModel() throws IOException;

//	public StatementTransformingException doesNotSupportException(
//			String message, int line, int column) {
//		return new StatementTransformingException(
//				"This version of transformer does not support "
//						+ message + ".", line, column);
//	}
}