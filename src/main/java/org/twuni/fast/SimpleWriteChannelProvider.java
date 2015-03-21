package org.twuni.fast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.twuni.fast.io.WriteChannel;

/**
 * Maintains an internal mapping of addresses to {@link WriteChannel}s.
 */
public class SimpleWriteChannelProvider implements WriteChannelProvider {

	private static String toLocator( byte [] address ) {
		return Integer.toHexString( Arrays.hashCode( address ) );
	}

	private final Map<String, Set<WriteChannel>> allChannels = new HashMap<String, Set<WriteChannel>>();

	@Override
	public void attach( byte [] address, WriteChannel channel ) {
		getWriteChannels( address ).add( channel );
	}

	@Override
	public void detach( byte [] address, WriteChannel channel ) {
		getWriteChannels( address ).remove( channel );
	}

	private Set<WriteChannel> getWriteChannels( byte [] address ) {
		String locator = toLocator( address );
		Set<WriteChannel> channels = allChannels.get( locator );
		if( channels == null ) {
			channels = new HashSet<WriteChannel>();
			allChannels.put( locator, channels );
		}
		return channels;
	}

	@Override
	public Set<WriteChannel> provideWriteChannels( byte [] address ) {
		return getWriteChannels( address );
	}

}
