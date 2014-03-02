package icbm.sentry.turret.ai;

import calclavia.lib.prefab.IServo;
import calclavia.lib.utility.MathUtility;
import calclavia.lib.utility.RotationManager;
import universalelectricity.api.vector.EulerAngle;

public class EulerServo extends EulerAngle
{
	public EulerAngle upperLimit = new EulerAngle(180, 40);
	public EulerAngle lowerLimit = new EulerAngle(-180, -40);
	private EulerAngle targetAngle = new EulerAngle();
	private double rotationSpeed;

	public EulerServo(EulerAngle angle, float rotationSpeed)
	{
		super(angle);
		this.rotationSpeed = rotationSpeed;
	}

	public EulerServo setLimit(EulerAngle upperLimit, EulerAngle lowerLimit)
	{
		this.upperLimit = upperLimit;
		this.lowerLimit = lowerLimit;
		return this;
	}

	public void update()
	{
		for (int i = 0; i < toArray().length; i++)
			updateAngle(i);
	}

	public void updateAngle(int index)
	{
		double currentAngle = toArray()[index];
		double targetAngle = this.targetAngle.toArray()[index];
		double upperLimit = this.upperLimit.toArray()[index];
		double lowerLimit = this.lowerLimit.toArray()[index];

		if (Math.abs(currentAngle - targetAngle) > rotationSpeed)
		{
			if (Math.abs(currentAngle - targetAngle) >= 180)
			{
				currentAngle += currentAngle > targetAngle ? rotationSpeed : -rotationSpeed;
			}
			else
			{
				currentAngle += currentAngle > targetAngle ? -rotationSpeed : rotationSpeed;
			}
		}
		else
		{
			currentAngle = targetAngle;
		}

		currentAngle = MathUtility.clampAngleTo180(currentAngle);

		if (currentAngle > upperLimit)
		{
			currentAngle = upperLimit;
		}
		else if (currentAngle < lowerLimit)
		{
			currentAngle = lowerLimit;
		}

		set(index, currentAngle);
	}

	public void setTargetRotation(EulerAngle targetRotation)
	{
		this.targetAngle = targetRotation;
	}
}
