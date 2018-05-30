package concurrentAddLayout.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import concurrentAddLayout.constants.ConcurrentAddLayoutPortletKeys;

/**
 * @author liferay
 */
@Component(immediate = true, property = { "com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true", "javax.portlet.display-name=concurrentAddLayout Portlet",
		"javax.portlet.init-param.template-path=/", "javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + ConcurrentAddLayoutPortletKeys.ConcurrentAddLayout,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user" }, service = Portlet.class)
public class ConcurrentAddLayoutPortlet extends MVCPortlet {

	private static final String GUEST = "Guest";
	private static final String TEST_LIFERAY_COM = "test@liferay.com";

	@Reference
	private LayoutLocalService layoutService;
	@Reference
	private UserLocalService userService;
	@Reference
	private CompanyLocalService companyLocalService;
	@Reference
	private GroupLocalService groupLocalService;

	private long groupId;

	@Activate
	public void active() {

		List<AddLayoutTask> tasks = Collections.emptyList();
		Company tmp;
		try {
			tmp = companyLocalService.getCompanyByWebId(PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID));
			// default super admin is test
			User test = userService.getUserByEmailAddress(tmp.getCompanyId(), TEST_LIFERAY_COM);
			// default group key of liferay.com
			Group importTarget = groupLocalService.getGroup(tmp.getCompanyId(), GUEST);
			groupId = importTarget.getGroupId();
			final String name = PrincipalThreadLocal.getName();
			tasks = Stream.of(1, 2, 3, 4).map(x -> ApiCallParameter.buildLayoutConf(importTarget.getGroupId()))
					.map(x -> new AddLayoutTask(layoutService, userService, x, test.getUserId(), name))
					.collect(Collectors.toList());

		} catch (PortalException e) {
			e.printStackTrace();
		}

		ExecutorService executor = Executors.newFixedThreadPool(2);
		tasks.stream().forEach(executor::execute);
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

	}

//	@Deactivate
//	public void cleanImportedLayouts() {
//		try {
//			layoutService.getLayouts(groupId, false, "portlet").stream()
//			.filter(x -> x.getName().startsWith(ApiCallParameter.CONCURRENT_LAYOUT_NAME)).forEach(this::deleteIt);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private void deleteIt(Layout x){
//	
//		layoutService.deleteLayout(x);
//	}
}