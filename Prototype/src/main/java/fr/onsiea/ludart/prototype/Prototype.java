/*
 *  Copyright 2021-2023 Onsiea All rights reserved.
 *
 *  This file is part of Ludart Game Framework project developed by Onsiea Studio.
 *  (https://github.com/OnsieaStudio/Ludart)
 *
 * Ludart is [licensed]
 *  (https://github.com/OnsieaStudio/Ludart/blob/main/LICENSE) under the terms of
 *  the "GNU General Public License v3.0" (GPL-3.0).
 *  https://github.com/OnsieaStudio/Ludart/wiki/License#license-and-copyright
 *
 *  Ludart is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3.0 of the License, or
 *  (at your option) any later version.
 *
 *  Ludart is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ludart. If not, see <https://www.gnu.org/licenses/>.
 *
 *  Neither the name "Onsiea", "Ludart", or any derivative name or the
 *  names of its authors / contributors may be used to endorse or promote
 *  products derived from this software and even less to name another project or
 *  other work without clear and precise permissions written in advance.
 *
 *  @Author : Seynax (https://github.com/seynax)
 *  @Organization : Onsiea Studio (https://github.com/OnsieaStudio)
 */

package fr.onsiea.ludart.prototype;

import java.nio.IntBuffer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

@Getter
@AllArgsConstructor
public class Prototype
{
	private final IPrototypeImpl prototypeImpl;
	private final int width;
	private final int height;
	private final String title;
	private final int    frameRate;
	private final int    updateRate;
	private final boolean sync;

	public final void start()
	{
		GLFWErrorCallback.createPrint(System.err).set();

		if ( !glfwInit() )
		{
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_REFRESH_RATE, frameRate);

		var windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
		if ( windowHandle == NULL )
		{
			throw new RuntimeException("Failed to create the GLFW window");
		}

		glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true);
		});

		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth  = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			glfwGetWindowSize(windowHandle, pWidth, pHeight);

			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			glfwSetWindowPos(
					windowHandle,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
			);
		}

		glfwMakeContextCurrent(windowHandle);
		glfwSwapInterval(sync ? 1 : 0);

		glfwShowWindow(windowHandle);

		GL.createCapabilities();

		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

		while (!glfwWindowShouldClose(windowHandle))
		{
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			glfwSwapBuffers(windowHandle);

			prototypeImpl.input(windowHandle);

			prototypeImpl.update();

			prototypeImpl.render();

			glfwPollEvents();
		}

		glfwFreeCallbacks(windowHandle);
		glfwDestroyWindow(windowHandle);

		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
}