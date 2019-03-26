/*******************************************************************************
 * Copyright 2018-2019 Espressif Systems (Shanghai) PTE LTD. All rights reserved.
 * Use is subject to license terms.
 *******************************************************************************/
package com.espressif.idf.ui.templates;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.cdt.cmake.core.CMakeNature;
import org.eclipse.cdt.cmake.core.CMakeProjectGenerator;
import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.build.CBuilder;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.framework.Bundle;

import com.espressif.idf.core.IDFProjectNature;
import com.espressif.idf.core.util.FileUtil;
import com.espressif.idf.ui.UIPlugin;

/**
 * IDF CMake project Generator
 * 
 * @author Kondal Kolipaka <kondal.kolipaka@espressif.com>
 */
public class IDFProjectGenerator extends CMakeProjectGenerator
{

	private File sourceTemplatePath;

	public IDFProjectGenerator(String manifestFile, File source)
	{
		super(manifestFile);
		this.sourceTemplatePath = source;
	}
	
	@Override
	protected void initProjectDescription(IProjectDescription description) {
		description.setNatureIds(
				new String[] { CProjectNature.C_NATURE_ID, CCProjectNature.CC_NATURE_ID, CMakeNature.ID, IDFProjectNature.ID });
		ICommand command = description.newCommand();
		CBuilder.setupBuilder(command);
		description.setBuildSpec(new ICommand[] { command });
	}

	@Override
	public void generate(Map<String, Object> model, IProgressMonitor monitor) throws CoreException
	{
		super.generate(model, monitor);
		if (sourceTemplatePath == null)
		{
			return; // let's go with the default generate
		}

		// Target project
		IProject project = getProject();

		// copy IDF template resources
		try
		{
			copyIDFTemplateToWorkspace(project.getName(), sourceTemplatePath, project);
		} catch (IOException e)
		{
			e.printStackTrace(); // something wrong with copying the template to the generated project
		}

		// refresh to see the copied resources in the project explorer
		project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}

	@Override
	public Bundle getSourceBundle()
	{
		return UIPlugin.getDefault().getBundle();
	}

	/**
	 * Copy template project resources into target project resource
	 * 
	 * @param projectName
	 * @param sourceTemplateFile
	 * @param targetProject
	 * @throws IOException
	 */
	protected void copyIDFTemplateToWorkspace(String projectName, File sourceTemplateFile, IProject targetProject)
			throws IOException
	{

		File projectFile = targetProject.getLocation().toFile();
		File[] files = sourceTemplateFile.listFiles();

		for (File file : files)
		{
			// create the file/directory
			File dest = new File(projectFile, file.getName());
			if (file.isDirectory())
			{
				FileUtil.copyDirectory(file, dest);
			} else
			{
				FileUtil.copyFile(file, dest);
			}
		}
	}
}