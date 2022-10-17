package scc.srv;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.core.Application;

public class MainApplication extends Application
{
	private final Set<Object> singletons = new HashSet<>();
	private final Set<Class<?>> resources = new HashSet<>();

	public MainApplication() {
		resources.add( ControlResource.class);
		singletons.add( new MediaResource());
		singletons.add( new UserResource());
		singletons.add( new AuctionResource());
	}

	@Override
	public Set<Class<?>> getClasses() {
		return resources;
	}

	@SuppressWarnings({"deprecation"})
	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
