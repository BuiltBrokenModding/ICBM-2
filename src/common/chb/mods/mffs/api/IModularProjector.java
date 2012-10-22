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

public interface IModularProjector {

	
	public boolean isLinkedSecStation();
//  true/false Projector ist linked to a Security Station
	
	public int getProjektor_Typ();
//  Typ of the Projector
//  1 = Wall
//  2 = Deflect
//  3 = Tube
//  4 = Cube
//  5 =  Sphere
//  6 = Conataiment
//  7 =  Advance Cube

	
	public boolean isLinkCapacitor();
//  true/false is linked to a ForcePower Relay
	
	public boolean isActive();
//  true / false is Projector Active
	
	
	
	
	
	
	
	
	
	
}
