package concurrentAddLayout.portlet;

import com.liferay.portal.kernel.exception.LayoutFriendlyURLException;
import com.liferay.portal.kernel.exception.LayoutFriendlyURLsException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AddLayoutTask implements Runnable {

	private final LayoutLocalService layoutService;
	private final UserLocalService userService;
	private final ApiCallParameter conf;
	private final long userId;
	private final String name;

	public AddLayoutTask(LayoutLocalService layoutService, UserLocalService userService, ApiCallParameter conf,
			long userId, String name) {
		super();
		this.layoutService = layoutService;
		this.userService = userService;
		this.conf = conf;
		this.userId = userId;
		this.name = name;
	}

	@Override
	public void run() {
		PermissionChecker newChecker = null;
		try {
			User importingUser = userService.getUser(userId);
			newChecker = PermissionCheckerFactoryUtil.create(importingUser);
			PermissionThreadLocal.setPermissionChecker(newChecker);
		} catch (Exception e) {
			e.printStackTrace();
		}

		PrincipalThreadLocal.setName(name);
		try {
			apiCall(conf);
		} catch (LayoutFriendlyURLsException e) {


			
			System.out.println("LayoutFriendlyURLsException Suppresed Types " + Arrays.asList(e.getSuppressed()).stream()
					.filter(LayoutFriendlyURLException.class::isInstance).map(LayoutFriendlyURLException.class::cast)
					.map(LayoutFriendlyURLException::getType).map(x->x.toString()).collect(Collectors.joining(";")));
			
			System.out.println("LayoutFriendlyURLsException Cause Types " + Arrays.asList(e.getCause()).stream()
					.filter(LayoutFriendlyURLException.class::isInstance).map(LayoutFriendlyURLException.class::cast)
					.map(LayoutFriendlyURLException::getType).map(x->x.toString()).collect(Collectors.joining(";")));

			
		} catch (PortalException e) {
			e.printStackTrace();
		}
	}

	private void apiCall(ApiCallParameter parameterObject) throws PortalException {
		synchronized (layoutService) {
			layoutService.addLayout(userId, parameterObject.groupId, parameterObject.privateLayout,
					parameterObject.parentLayoutId, parameterObject.name, parameterObject.title,
					parameterObject.description, parameterObject.type, parameterObject.hidden, parameterObject.friendlyURL,
					parameterObject.serviceContext);
		}
	}

}
