package de.jurihock.voicesmith;

public enum FrameType
{
	Large(2),
	Default(1),
	Medium(1 / 2D),
	Small(1 / 4D);

	public final double	ratio;

	private FrameType(double ratio)
	{
		this.ratio = ratio;
	}
}