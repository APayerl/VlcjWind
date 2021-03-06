/*
VlcjWind makes it easier to use VLCJ and it's DirectMediaPlayer for multiple frames
Copyright (C) 2017  Anders Payerl

VlcjWind is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

VlcjWind is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with VlcjWind.  If not, see <http://www.gnu.org/licenses/>.

For contact use email anders{dot}payerl{at}gmail{dot}com
*/

package se.payerl.vlcjwind;

import java.awt.image.BufferedImage;

@FunctionalInterface
public interface FrameListener
{
	public void newFrameRecieved(BufferedImage image);
}