package scc.srv;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import scc.utils.GenericExceptionMapper;

@ApplicationPath("/rest")
public class MainApplication extends Application
{
	private final Set<Object> singletons = new HashSet<>();
	private final Set<Class<?>> resources = new HashSet<>();

	public MainApplication() {
		if (!System.getenv().containsKey("production"))
			resources.add( GenericExceptionMapper.class );
		resources.add( ControlResource.class );
		resources.add( MediaResource.class );
		resources.add( UserResource.class );
		resources.add( AuctionResource.class );
	}

	@Override
	public Set<Class<?>> getClasses() {
		return resources;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
