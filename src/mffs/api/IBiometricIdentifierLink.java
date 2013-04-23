package mffs.api;

import mffs.api.security.IBiometricIdentifier;

/**
 * Applied to TileEntities that is linked with a Biometric Identifier.
 * 
 * @author Calclavia
 * 
 */
public interface IBiometricIdentifierLink
{
	public IBiometricIdentifier getBiometricIdentifier();

}
