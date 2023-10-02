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

package fr.onsiea.ludart.prototype.tests;

import fr.onsiea.ludart.prototype.IPrototypeImpl;
import fr.onsiea.ludart.prototype.Prototype;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.lwjgl.BufferUtils.*;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class PrototypeSound implements IPrototypeImpl
{
	public static void main(String[] args)
	{
		var prototype = new Prototype(new PrototypeSound(), 1920, 1080, "Sound Prototype !", 60, 60, true);

		prototype.start();
	}

	private final SoundManager soundManager;
	private long last;
	private Random random;
	private ISoundSource[] sounds;

	public PrototypeSound()
	{
		try
		{
			soundManager = new SoundManager();
			random = new Random();

			sounds = new ISoundSource[5];
			var soundSource = new SoundSourceMultiplesBuffers(false, false);
			int soundIndex = 0;
			sounds[soundIndex++] = soundSource;
			for(int i = 0; i < 7; i ++)
			{
				var soundBuffer = new SoundBuffer("Ludart\\Prototype\\src\\test\\resources\\sounds\\anvil\\anvil-hit_" + i + ".ogg");
				soundSource.addBuffer(soundBuffer.bufferId);
				soundManager.addSoundBuffer(soundBuffer);
			}
			soundManager.addSoundSource("anvil-hits", soundSource);


			soundSource = new SoundSourceMultiplesBuffers(false, false);
			sounds[soundIndex++] = soundSource;
			for(int i = 0; i < 5; i ++)
			{
				var soundBuffer = new SoundBuffer("Ludart\\Prototype\\src\\test\\resources\\sounds\\anvil\\anvil-hit-group_" + i + ".ogg");
				soundSource.addBuffer(soundBuffer.bufferId);
				soundManager.addSoundBuffer(soundBuffer);
			}
			soundManager.addSoundSource("anvil-hit-groups", soundSource);

			soundSource = new SoundSourceMultiplesBuffers(false, false);
			sounds[soundIndex++] = soundSource;
			var soundBuffer = new SoundBuffer("Ludart\\Prototype\\src\\test\\resources\\sounds\\anvil\\anvil-clanks_0.ogg");
			soundSource.addBuffer(soundBuffer.bufferId);
			soundManager.addSoundBuffer(soundBuffer);
			soundManager.addSoundSource("anvil-clanks", soundSource);

			soundSource = new SoundSourceMultiplesBuffers(false, false);
			sounds[soundIndex++] = soundSource;
			soundBuffer = new SoundBuffer("Ludart\\Prototype\\src\\test\\resources\\sounds\\anvil\\anvil-knife-scrape-and-hit_0.ogg");
			soundSource.addBuffer(soundBuffer.bufferId);
			soundManager.addSoundBuffer(soundBuffer);
			soundManager.addSoundSource("anvil-knife-scrape-and-hit", soundSource);

			soundSource = new SoundSourceMultiplesBuffers(false, false);
			sounds[soundIndex++] = soundSource;
			soundBuffer = new SoundBuffer("Ludart\\Prototype\\src\\test\\resources\\sounds\\anvil\\anvil-metal-punch_0.ogg");
			soundSource.addBuffer(soundBuffer.bufferId);
			soundManager.addSoundBuffer(soundBuffer);
			soundManager.addSoundSource("anvil-metal-punch", soundSource);

		}
		catch (Exception eIn)
		{
			throw new RuntimeException(eIn);
		}

		last = System.nanoTime();
		waitTime = (random.nextInt(max - min) + min) % max;
		var soundType = random.nextInt(sounds.length);
		currentSound = sounds[soundType];
		if(currentSound instanceof SoundSourceMultiplesBuffers)
		{
			var soundVariation = random.nextInt(((SoundSourceMultiplesBuffers) currentSound).buffers.size());
			((SoundSourceMultiplesBuffers) currentSound).selectBuffer(soundVariation);
			System.out.println(soundType + "[" + soundVariation + "], " + number + " : " + waitTime);
		}
		else
		{
			System.out.println(soundType + ", " + number + " : " + waitTime);
		}

		number   = random.nextInt( 100);
		if(number > 0 && number < 10)
		{
			number = 6;
		}
		else if(number >= 10 && number < 25)
		{
			number = 5;
		}
		else if(number >= 25 && number < 45)
		{
			number = 4;
		}
		else if(number >= 45 && number < 70)
		{
			number = 2;
		}
		else
		{
			number = 1;
		}
	}

	@Override
	public void input(long windowHandleIn)
	{

	}

	private int min = 2_000_000_00;
	private int max = 2_000_000_000;
	private int number;
	private int i;
	private ISoundSource currentSound;

	private long waitTime;
	@Override
	public void update()
	{
		min = 1_000_000_00;
		max = 1_000_000_000;
		if(System.nanoTime() - last > waitTime && !currentSound.isPlaying())
		{
			last = System.nanoTime();

			var variation = random.nextInt(8_000_000_00) - 6_500_000_00;
			waitTime += variation;

			if(i > number)
			{
				waitTime = (long) (((random.nextInt((max - min)) + min) % (max)));
				var soundType = random.nextInt(sounds.length);
				currentSound = sounds[soundType];
				if(currentSound instanceof SoundSourceMultiplesBuffers)
				{
					var soundVariation = random.nextInt(((SoundSourceMultiplesBuffers) currentSound).buffers.size());
					((SoundSourceMultiplesBuffers) currentSound).selectBuffer(soundVariation);
					System.out.println(soundType + "[" + soundVariation + "], " + number + " : " + waitTime);
				}
				else
				{
					System.out.println(soundType + ", " + number + " : " + waitTime);
				}

				number   = random.nextInt( 100);
				if(number > 0 && number < 10)
				{
					number = 6;
				}
				else if(number >= 10 && number < 25)
				{
					number = 5;
				}
				else if(number >= 25 && number < 45)
				{
					number = 4;
				}
				else if(number >= 45 && number < 70)
				{
					number = 2;
				}
				else
				{
					number = 1;
				}

				i = 0;
				System.out.println(soundType + ", " + number + " : " + waitTime);
				currentSound.stop();
				currentSound.play();
			}
			i ++;
		}
	}

	@Override
	public void render()
	{

	}

	public static class SoundBuffer
	{
		private final @Getter int bufferId;

		private ShortBuffer pcm = null;

		private ByteBuffer vorbis = null;

		public SoundBuffer(String file) throws Exception
		{
			this.bufferId = alGenBuffers();

			try (STBVorbisInfo info = STBVorbisInfo.malloc())
			{
				ShortBuffer pcm = readVorbis(file, 32 * 1024, info);

				alBufferData(bufferId, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());
			}
		}

		private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity)
		{
			ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);

			buffer.flip();
			newBuffer.put(buffer);

			return newBuffer;
		}

		public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException
		{
			ByteBuffer buffer;

			Path path = Paths.get(resource);
			if (Files.isReadable(path))
			{
				try (SeekableByteChannel fc = Files.newByteChannel(path))
				{
					buffer = createByteBuffer((int) fc.size() + 1);
					while (fc.read(buffer) != -1) ;
				}
			}
			else
			{
				try
						(
								InputStream source = new FileInputStream(resource);
								ReadableByteChannel rbc = Channels.newChannel(source)) {
					buffer = createByteBuffer(bufferSize);

					while (true) {
						int bytes = rbc.read(buffer);
						if (bytes == -1) {
							break;
						}
						if (buffer.remaining() == 0) {
							buffer = resizeBuffer(buffer, buffer.capacity() * 2);
						}
					}
				}
			}

			buffer.flip();
			return buffer;
		}


		private ShortBuffer readVorbis(String resource, int bufferSize, STBVorbisInfo info) throws Exception
		{
			try (MemoryStack stack = MemoryStack.stackPush())
			{
				vorbis = ioResourceToByteBuffer(resource, bufferSize);
				IntBuffer error = stack.mallocInt(1);
				long decoder = stb_vorbis_open_memory(vorbis, error, null);
				if (decoder == NULL)
				{
					throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
				}

				stb_vorbis_get_info(decoder, info);

				int channels = info.channels();

				int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

				pcm = MemoryUtil.memAllocShort(lengthSamples);

				pcm.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
				stb_vorbis_close(decoder);

				return pcm;
			}
		}

		public void cleanup()
		{
			alDeleteBuffers(this.bufferId);
			if (pcm != null)
			{
				MemoryUtil.memFree(pcm);
			}
		}
	}

	public interface ISoundSource
	{
		void setPosition(Vector3f position);

		void setSpeed(Vector3f speed);

		void setGain(float gain);

		void setProperty(int param, float value);

		void play();

		boolean isPlaying();

		void pause();

		void stop();

		void cleanup();
	}

	public static class SoundSourceMultiplesBuffers implements ISoundSource
	{
		private final int sourceId;
		private final List<Integer> buffers;

		public SoundSourceMultiplesBuffers(boolean loop, boolean relative) {
			this.sourceId = alGenSources();
			if (loop)
			{
				alSourcei(sourceId, AL_LOOPING, AL_TRUE);
			}
			if (relative)
			{
				alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_TRUE);
			}
			buffers = new ArrayList<>();
		}

		public void addBuffer(int bufferId)
		{
			buffers.add(bufferId);
			stop();
			alSourcei(sourceId, AL_BUFFER, bufferId);
		}

		public void selectBuffer(int indexIn)
		{
			if(indexIn >= buffers.size())
			{
				System.err.println("SoundSourceMultiplesBuffer : Index > length " + indexIn);
			}

			stop();
			alSourcei(sourceId, AL_BUFFER, buffers.get(indexIn));
		}

		public void setPosition(Vector3f position)
		{
			alSource3f(sourceId, AL_POSITION, position.x, position.y, position.z);
		}

		public void setSpeed(Vector3f speed)
		{
			alSource3f(sourceId, AL_VELOCITY, speed.x, speed.y, speed.z);
		}

		public void setGain(float gain)
		{
			alSourcef(sourceId, AL_GAIN, gain);
		}

		public void setProperty(int param, float value)
		{
			alSourcef(sourceId, param, value);
		}

		public void play()
		{
			alSourcePlay(sourceId);
		}

		public boolean isPlaying()
		{
			return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
		}

		public void pause()
		{
			alSourcePause(sourceId);
		}

		public void stop()
		{
			alSourceStop(sourceId);
		}

		public void cleanup()
		{
			stop();
			alDeleteSources(sourceId);
		}
	}

	public static class SoundSource implements ISoundSource
	{
		private final int sourceId;

		public SoundSource(boolean loop, boolean relative) {
			this.sourceId = alGenSources();
			if (loop)
			{
				alSourcei(sourceId, AL_LOOPING, AL_TRUE);
			}
			if (relative)
			{
				alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_TRUE);
			}
		}

		public void setBuffer(int bufferId)
		{
			stop();
			alSourcei(sourceId, AL_BUFFER, bufferId);
		}

		public void setPosition(Vector3f position)
		{
			alSource3f(sourceId, AL_POSITION, position.x, position.y, position.z);
		}

		public void setSpeed(Vector3f speed)
		{
			alSource3f(sourceId, AL_VELOCITY, speed.x, speed.y, speed.z);
		}

		public void setGain(float gain)
		{
			alSourcef(sourceId, AL_GAIN, gain);
		}

		public void setProperty(int param, float value)
		{
			alSourcef(sourceId, param, value);
		}

		public void play()
		{
			alSourcePlay(sourceId);
		}

		public boolean isPlaying()
		{
			return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
		}

		public void pause()
		{
			alSourcePause(sourceId);
		}

		public void stop()
		{
			alSourceStop(sourceId);
		}

		public void cleanup()
		{
			stop();
			alDeleteSources(sourceId);
		}
	}

	public class SoundListener
	{
		public SoundListener()
		{
			this(new Vector3f(0, 0, 0));
		}

		public SoundListener(Vector3f position)
		{
			alListener3f(AL_POSITION, position.x, position.y, position.z);
			alListener3f(AL_VELOCITY, 0, 0, 0);

		}

		public void setSpeed(Vector3f speed)
		{
			alListener3f(AL_VELOCITY, speed.x, speed.y, speed.z);
		}

		public void setPosition(Vector3f position)
		{
			alListener3f(AL_POSITION, position.x, position.y, position.z);
		}

		public void setOrientation(Vector3f at, Vector3f up)
		{
			float[] data = new float[6];
			data[0] = at.x;
			data[1] = at.y;
			data[2] = at.z;
			data[3] = up.x;
			data[4] = up.y;
			data[5] = up.z;
			alListenerfv(AL_ORIENTATION, data);
		}
	}

	public static class SoundManager
	{

		private long device;

		private long context;

		private SoundListener listener;

		private final List<SoundBuffer> soundBufferList;

		private final Map<String, ISoundSource> soundSourceMap;

		public SoundManager() throws Exception
		{
			soundBufferList = new ArrayList<>();
			soundSourceMap  = new HashMap<>();

			this.device = alcOpenDevice((ByteBuffer) null);
			if (device == NULL)
			{
				throw new IllegalStateException("Failed to open the default OpenAL device.");
			}
			ALCCapabilities deviceCaps = ALC.createCapabilities(device);
			this.context = alcCreateContext(device, (IntBuffer) null);
			if (context == NULL)
			{
				throw new IllegalStateException("Failed to create OpenAL context.");
			}
			alcMakeContextCurrent(context);
			AL.createCapabilities(deviceCaps);
		}



		public void addSoundSource(String name, ISoundSource soundSource)
		{
			this.soundSourceMap.put(name, soundSource);
		}

		public ISoundSource getSoundSource(String name) {
			return this.soundSourceMap.get(name);
		}

		public void restartSoundSource(String name)
		{
			ISoundSource soundSource = this.soundSourceMap.get(name);
			if (soundSource == null)
			{
				return;
			}

			if(soundSource.isPlaying())
			{
				soundSource.stop();
			}

			soundSource.play();
		}

		public void stopSoundSource(String name)
		{
			ISoundSource soundSource = this.soundSourceMap.get(name);
			if (soundSource != null && soundSource.isPlaying()) {
				soundSource.stop();
			}
		}

		public void playSoundSource(String name) {
			ISoundSource soundSource = this.soundSourceMap.get(name);
			if (soundSource != null && !soundSource.isPlaying()) {
				soundSource.play();
			}
		}

		public void removeSoundSource(String name) {
			this.soundSourceMap.remove(name);
		}

		public void addSoundBuffer(SoundBuffer soundBuffer) {
			this.soundBufferList.add(soundBuffer);
		}

		public SoundListener getListener() {
			return this.listener;
		}

		public void setListener(SoundListener listener) {
			this.listener = listener;
		}

		public void updateListenerPosition(Vector3f positionIn, Vector3f orientationIn)
		{
			// Update camera matrix with camera data
			//Transformation.updateGenericViewMatrix(positionIn, orientationIn), cameraMatrix);

			listener.setPosition(positionIn);
			//Vector3f at = new Vector3f();
			//cameraMatrix.positiveZ(at).negate();
			//Vector3f up = new Vector3f();
			//cameraMatrix.positiveY(up);
			//listener.setOrientation(at, up);
		}

		public void setAttenuationModel(int model) {
			alDistanceModel(model);
		}

		public void cleanup() {
			for (var soundSource : soundSourceMap.values()) {
				soundSource.cleanup();
			}
			soundSourceMap.clear();
			for (var soundBuffer : soundBufferList) {
				soundBuffer.cleanup();
			}
			soundBufferList.clear();
			if (context != NULL)
			{
				alcDestroyContext(context);
			}
			if (device != NULL)
			{
				alcCloseDevice(device);
			}
		}
	}
}