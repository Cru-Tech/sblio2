package org.ccci.framework.sblio;

import java.util.ArrayList;
import java.util.List;

import org.ccci.framework.provider.IResourceProvider;
import org.ccci.framework.user.IUser;
import org.ccci.util.ServletProperties;

import com.siebel.data.SiebelException;

public class PooledSiebelResourceProvider implements IResourceProvider
{

    public static final String NAME = "PooledSiebelResourseProvider";
    protected boolean useDev = true;
    
    private static List resourceNames = null;
    
    static
    {
        resourceNames = new ArrayList(1);
        resourceNames.add(DssDataBean.NAME);
    }
	
    public PooledSiebelResourceProvider(){}
    
    public PooledSiebelResourceProvider(boolean useDev)
    {
        this.useDev = useDev;
    }
    
    public PooledSiebelResourceProvider(String type, ServletProperties properties)
    {
    	
    }
    
	public void expireOldResources()
	{
		return;
	}

	public Object getResource(String name, IUser user) throws Exception
	{
		if(resourceNames.contains(name))
		{
			IDssDataBean bean = SiebelDataBeanPoolList.getInstance().getDataBean(useDev?"DEV":"TEST", null, null, null);
			try
			{
				bean.getDataBean().getBusObject("Account");
				bean.reset();
				return bean;
			}
			catch(SiebelException se)
			{
				SiebelDataBeanPoolList.getInstance().killDataBean(bean);
				bean = SiebelDataBeanPoolList.getInstance().getDataBean(useDev?"DEV":"DEV", null, null, null);
				bean.getDataBean().getBusObject("Account");
				bean.reset();
				return bean;	
			}
		}
		return null;
	}

	public List getResourcesProvided()
	{
		return resourceNames;
	}

	public void releaseResource(Object resource, IUser user) throws Exception
	{
		if(resource instanceof IDssDataBean)
		{
			IDssDataBean databean = (IDssDataBean)resource;
			databean.reset();
			SiebelDataBeanPoolList.getInstance().releaseDataBean(databean);
		}

	}

	public String getName()
	{
		return this.NAME;
	}

}
