# liferay-layout-issue

 When trying to concurrently add layouts in liferay dxp a StaleObjectException occurs: 
 
_This repository contains a maven osgi portlet to reproduce the exception._
 
 env: DXP SP6,SP7; Fixpack-de 40,48
 
The context is a parallel import of liferay layouts through a liferay portlet; build with spring/osgi. When executing it in Liferay dxp, the api call to add a Layout throws a StaleObjectStateException. 

This exception occurs when the api internally does an update on the corresponding LayoutSet (updating the PageCount for that Group, where the layout has been added to, just a single moment ago).

This does not happen in a single threaded execution!


## Actions

* Firstly i synchronized that call .. without any better results
* meanwhile i read something about, that only synchronizing the threading won´t help, because the transaction itself may not be inside the synchronized execution block. therefore i also added a transactional annotation. .. without better results

so far i gained the following insight:

* there is a Bug in the LayoutSetLocalService.updatePageCount(): the updated LayoutSet is not returned .. therefore the (with Liferay 7/DXP introduced) mvcc Version of the LayoutSet is not incremented. .. but this should not have any influences on my situation (https://github.com/liferay/liferay-portal/blob/7eb86ce5f6a7b2c9a405853a20fe81592e639219/portal-impl/src/com/liferay/portal/service/impl/LayoutSetLocalServiceImpl.java).

## Question: 

* is this a consequence of the optimistic locking and i have to live with that? did i missed a puzzle when creating the threads ? maybe some weird .. configurate my hibernate session ... thing ? is it possible to add layouts concurrently ? 
  
 SO Entry Reference : https://stackoverflow.com/questions/50528192/concurrent-api-layoutlocalservice-addlayout-throws-staleobjectstateexception-in

```
 2018-05-30 13:25:51.009 INFO  [com.liferay.portal.kernel.deploy.auto.AutoDeployScanner][AutoDeployDir:263] Processing concurrentAddLayout-1.0.8-SNAPSHOT.jar
Exception in thread "pool-13-thread-1" com.liferay.portal.kernel.exception.SystemException: com.liferay.portal.kernel.dao.orm.ORMException: {mvccVersion=69, layoutSetId=20144, groupId=20142, companyId=20115, createDate=Mon May 14 06:58:17 GMT 2018, modifiedDate=Wed May 30 13:25:59 GMT 2018, privateLayout=false, logoId=27720, themeId=classic_WAR_classictheme, colorSchemeId=01, css=, pageCount=31, settings=, layoutSetPrototypeUuid=, layoutSetPrototypeLinkEnabled=false} is stale in comparison to {mvccVersion=70, layoutSetId=20144, groupId=20142, companyId=20115, createDate=2018-05-14 06:58:17.116, modifiedDate=2018-05-30 13:25:59.475, privateLayout=false, logoId=27720, themeId=classic_WAR_classictheme, colorSchemeId=01, css=, pageCount=30, settings=, layoutSetPrototypeUuid=, layoutSetPrototypeLinkEnabled=false}
	at com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl.processException(BasePersistenceImpl.java:270)
	at com.liferay.portal.service.persistence.impl.LayoutSetPersistenceImpl.updateImpl(LayoutSetPersistenceImpl.java:1947)
	at com.liferay.portal.service.persistence.impl.LayoutSetPersistenceImpl.updateImpl(LayoutSetPersistenceImpl.java:72)
	at com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl.update(BasePersistenceImpl.java:352)
	at com.liferay.portal.service.impl.LayoutSetLocalServiceImpl.updatePageCount(LayoutSetLocalServiceImpl.java:418)
	at sun.reflect.GeneratedMethodAccessor1403.invoke(Unknown Source)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at com.liferay.portal.spring.aop.ServiceBeanMethodInvocation.proceed(ServiceBeanMethodInvocation.java:163)
	at com.liferay.portal.service.impl.LayoutSetLocalServiceStagingAdvice.invoke(LayoutSetLocalServiceStagingAdvice.java:42)
	at com.liferay.portal.spring.aop.ServiceBeanMethodInvocation.proceed(ServiceBeanMethodInvocation.java:137)
	at com.liferay.portal.spring.transaction.DefaultTransactionExecutor.execute(DefaultTransactionExecutor.java:54)
	at com.liferay.portal.spring.transaction.TransactionInterceptor.invoke(TransactionInterceptor.java:58)
	at com.liferay.portal.spring.aop.ServiceBeanMethodInvocation.proceed(ServiceBeanMethodInvocation.java:137)
	at com.liferay.portal.spring.aop.ChainableMethodAdvice.invoke(ChainableMethodAdvice.java:56)
	at com.liferay.portal.spring.aop.ServiceBeanMethodInvocation.proceed(ServiceBeanMethodInvocation.java:137)
	at com.liferay.portal.spring.aop.ServiceBeanAopProxy.invoke(ServiceBeanAopProxy.java:169)
	at com.sun.proxy.$Proxy174.updatePageCount(Unknown Source)
	at com.liferay.portal.service.impl.LayoutLocalServiceImpl.addLayout(LayoutLocalServiceImpl.java:335)
	at com.liferay.portal.service.impl.LayoutLocalServiceImpl.addLayout(LayoutLocalServiceImpl.java:420)
	at sun.reflect.GeneratedMethodAccessor1837.invoke(Unknown Source)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at com.liferay.portal.spring.aop.ServiceBeanMethodInvocation.proceed(ServiceBeanMethodInvocation.java:163)
	at com.liferay.portal.service.impl.LayoutLocalServiceStagingAdvice.invoke(LayoutLocalServiceStagingAdvice.java:137)
	at com.liferay.portal.spring.aop.ServiceBeanMethodInvocation.proceed(ServiceBeanMethodInvocation.java:137)
	at com.liferay.portal.spring.transaction.DefaultTransactionExecutor.execute(DefaultTransactionExecutor.java:54)
	at com.liferay.portal.spring.transaction.TransactionInterceptor.invoke(TransactionInterceptor.java:58)
	at com.liferay.portal.spring.aop.ServiceBeanMethodInvocation.proceed(ServiceBeanMethodInvocation.java:137)
	at com.liferay.portal.service.ServiceContextAdvice.invoke(ServiceContextAdvice.java:51)
	at com.liferay.portal.spring.aop.ServiceBeanMethodInvocation.proceed(ServiceBeanMethodInvocation.java:137)
	at com.liferay.portal.spring.aop.ChainableMethodAdvice.invoke(ChainableMethodAdvice.java:56)
	at com.liferay.portal.spring.aop.ServiceBeanMethodInvocation.proceed(ServiceBeanMethodInvocation.java:137)
	at com.liferay.portal.spring.aop.ServiceBeanAopProxy.invoke(ServiceBeanAopProxy.java:169)
	at com.sun.proxy.$Proxy86.addLayout(Unknown Source)
	at concurrentAddLayout.portlet.AddLayoutTask.apiCall(AddLayoutTask.java:69)
	at concurrentAddLayout.portlet.AddLayoutTask.run(AddLayoutTask.java:48)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
Caused by: com.liferay.portal.kernel.dao.orm.ORMException: {mvccVersion=69, layoutSetId=20144, groupId=20142, companyId=20115, createDate=Mon May 14 06:58:17 GMT 2018, modifiedDate=Wed May 30 13:25:59 GMT 2018, privateLayout=false, logoId=27720, themeId=classic_WAR_classictheme, colorSchemeId=01, css=, pageCount=31, settings=, layoutSetPrototypeUuid=, layoutSetPrototypeLinkEnabled=false} is stale in comparison to {mvccVersion=70, layoutSetId=20144, groupId=20142, companyId=20115, createDate=2018-05-14 06:58:17.116, modifiedDate=2018-05-30 13:25:59.475, privateLayout=false, logoId=27720, themeId=classic_WAR_classictheme, colorSchemeId=01, css=, pageCount=30, settings=, layoutSetPrototypeUuid=, layoutSetPrototypeLinkEnabled=false}
	at com.liferay.portal.dao.orm.hibernate.ExceptionTranslator.translate(ExceptionTranslator.java:47)
	at com.liferay.portal.dao.orm.hibernate.SessionImpl.merge(SessionImpl.java:244)
	at com.liferay.portal.kernel.dao.orm.ClassLoaderSession.merge(ClassLoaderSession.java:410)
	at com.liferay.portal.service.persistence.impl.LayoutSetPersistenceImpl.updateImpl(LayoutSetPersistenceImpl.java:1943)
	... 38 more
Caused by: org.hibernate.StaleObjectStateException: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): [com.liferay.portal.model.impl.LayoutSetImpl#20144]
	at org.hibernate.event.def.DefaultMergeEventListener.entityIsDetached(DefaultMergeEventListener.java:485)
	at org.hibernate.event.def.DefaultMergeEventListener.onMerge(DefaultMergeEventListener.java:255)
	at org.hibernate.event.def.DefaultMergeEventListener.onMerge(DefaultMergeEventListener.java:84)
	at org.hibernate.impl.SessionImpl.fireMerge(SessionImpl.java:867)
	at org.hibernate.impl.SessionImpl.merge(SessionImpl.java:851)
	at org.hibernate.impl.SessionImpl.merge(SessionImpl.java:855)
	at com.liferay.portal.dao.orm.hibernate.SessionImpl.merge(SessionImpl.java:241)
... 40 more
```
