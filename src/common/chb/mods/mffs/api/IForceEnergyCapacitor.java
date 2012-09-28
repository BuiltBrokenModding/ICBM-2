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
	// Charging status in %
	public int getCapacity();
	
	//Max ForcePower for this Generator
	public int getMaxForcePower();
	
	// ForcePower left in his Generator
	public int getForcePower();

	// Range of maximum transmit
	public int getTransmitRange();

	//count of paired device
	public Short getLinketProjektor();
	
	//  draws Force Energy from Capacitor 
	//  magnitude in %
	//  if 100% Forcefield turns off until enough Force Energy available
	public void EMPulse(int magnitude);

}
