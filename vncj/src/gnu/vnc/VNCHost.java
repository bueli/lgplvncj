package gnu.vnc;

/**
* <br><br><center><table border="1" width="80%"><hr>
* <strong><a href="http://www.amherst.edu/~tliron/vncj">VNCj</a></strong>
* <p>
* Copyright (C) 2000-2002 by Tal Liron
* <p>
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public License
* as published by the Free Software Foundation; either version 2.1
* of the License, or (at your option) any later version.
* <p>
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* <a href="http://www.gnu.org/copyleft/lesser.html">GNU Lesser General Public License</a>
* for more details.
* <p>
* You should have received a copy of the <a href="http://www.gnu.org/copyleft/lesser.html">
* GNU Lesser General Public License</a> along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
* <hr></table></center>
**/

import gnu.rfb.server.DefaultRFBAuthenticator;
import gnu.rfb.server.RFBAuthenticator;
import gnu.rfb.server.RFBHost;
import gnu.rfb.server.RFBServer;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class VNCHost
{
	//
	// Main
	//

	public static void main( String args[] )
	{
		//VirtualToolkit.setDefaultToolkit( "gnu.awt.virtual.swing.VirtualSwingToolkit" );
		
		ResourceBundle properties = null;
		try
		{
			properties = ResourceBundle.getBundle( "vncj" );
		}
		catch( MissingResourceException x )
		{
			System.err.println( "Can't load 'vncj.properties'." );
			return;
		}

		// Authenticator
                RFBAuthenticator authenticator = new DefaultRFBAuthenticator("password");
		
		// Create RFB hosts and web servers
		String key, serverClassName, displayName;
		Class<? extends RFBServer> serverClass;
		int display;
		for( Enumeration<String> e = properties.getKeys(); e.hasMoreElements(); )
		{
			key = e.nextElement();
			if( key.startsWith( "class." ) )
			{
				display = Integer.parseInt( key.substring( 6 ) );
				serverClassName = properties.getString( key );
				displayName = properties.getString( "name." + display );
				
				System.out.println( displayName );
				
				try
				{
					 Object instance = Class.forName( serverClassName );
					 if (instance instanceof RFBServer == false) {
						 throw new IllegalArgumentException("Class " + serverClassName + " does not extend " + RFBServer.class.getName());
					 }
					 serverClass = (Class<? extends RFBServer>) instance;
					// RFB host
					new RFBHost( display, displayName, serverClass, authenticator );
					
					// Webserver
					new WebServer( display, displayName, 800, 600 );
					
					System.out.println( "  VNC display " + display );
					System.out.println( "  Web server on port " + ( 5800 + display ) );
					System.out.println( "  Class: " + serverClassName );
				}
				catch( ClassNotFoundException x )
				{
					System.out.println( "  Could not load class: " + serverClassName );
				}
				catch( NoSuchMethodException x )
				{
					System.out.println( "  Unsupported class: " + serverClassName );
				}
			}
		}
	}
}
