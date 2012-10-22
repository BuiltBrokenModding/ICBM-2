/*  
    Copyright (C) 2012 Thunderdark

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    Contributors:
    Thunderdark - initial implementation
*/

package chb.mods.mffs.api;

public interface IForceEnergyCapacitor
{
	/**
	 * @return Charging status in %
	 */
	public int getCapacity();
	
	/**
	 * @return Max ForcePower for this Generator
	 */
	public int getMaxForcePower();
	
	/**
	 * @return ForcePower left in his Generator
	 */
	public int getForcePower();

	/**
	 * @return Range of maximum transmit
	 */
	public int getTransmitRange();

	/**
	 * @return Count of paired device
	 */
	public Short getLinketProjektor();
	
	/**
	 * Draws Force Energy from the Capacitor.
	 * @param magnitude in % from 0% to 100%.
	 */
	public void onEMPPulse(int magnitude);

}
