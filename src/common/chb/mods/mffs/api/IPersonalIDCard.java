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

import net.minecraft.src.ItemStack;

public interface IPersonalIDCard {
	
	public  String getUsername(ItemStack itemstack);
// get saved Username or Nobody on Error  Itemstack
	
	public int getSecLevel(ItemStack itemstack);

// get saved Securtiy Level for his Card 
// 0 = Error 
// 1 = Limited Access
// 2 = Full Access 
	

}
