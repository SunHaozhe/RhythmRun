package com.example.raphaelattali.rhythmrun.music.waveFileReaderLib;

public class WavFileException extends Exception
{
	public WavFileException()
	{
		super();
	}

	public WavFileException(String message)
	{
		super(message);
	}

	public WavFileException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public WavFileException(Throwable cause) 
	{
		super(cause);
	}
}
