package concurrentAddLayout.portlet;

import com.liferay.portal.kernel.service.ServiceContext;

public class ApiCallParameter {
	public static final String CONCURRENT_LAYOUT_NAME = "concurrent_layoutName_";
	public static final String CONCURRENT_LAYOUT_DESCRIPTION = "concurrent_layout_description_";
	public static final String CONCURRENT_LAYOUT2 = "concurrent/layout_";
	public static final String CONCURRENT_LAYOUT = "/concurrent/layout";
	private static int count = 0;

	public static ApiCallParameter buildLayoutConf(long groupId) {
		return new ApiCallParameter(groupId);
	}

	public long groupId;
	public ServiceContext serviceContext;
	public String friendlyURL = CONCURRENT_LAYOUT;
	public boolean hidden = false;
	public String type;
	public String description = CONCURRENT_LAYOUT2;
	public String title = CONCURRENT_LAYOUT_DESCRIPTION;
	public String name = CONCURRENT_LAYOUT_NAME;
	public long parentLayoutId = 0;
	public boolean privateLayout = false;

	private ApiCallParameter(long groupId) {
		this.groupId = groupId;
		ServiceContext sc = new ServiceContext();
		sc.setScopeGroupId(groupId);
		this.serviceContext = sc;
		this.friendlyURL = friendlyURL + count;
		this.description = description + count;
		this.title = title + count;
		this.name = name + count;
		this.type = "portlet";
		count++;
	}

}