package concurrentAddLayout.portlet;

import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.model.impl.LayoutModelImpl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor({"com.liferay.portal.model.impl.LayoutImpl","com.liferay.portal.model.impl.LayoutModelImpl"})
@PrepareForTest({LayoutModelImpl.class})
public class TestFriendlyUrl {

	
	@Test
	public void test() {
		PowerMockito.suppress(PowerMockito.fields(LayoutModelImpl.class));
		ApiCallParameter conf = ApiCallParameter.buildLayoutConf(0);
		Assert.assertEquals(-1, LayoutImpl.validateFriendlyURL(conf.friendlyURL)); ;
	}

}
