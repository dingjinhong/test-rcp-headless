package com.ichir.projects.p2update.plugin1;

import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.ichir.projects.p2update.plugin1.utilities.P2Util;
import com.ichir.projects.p2update.plugin1.utilities.UpdateJob;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(400, 300));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		configurer.setTitle("RCP Application");
	}
	
	@Override
	public void postWindowOpen() {
		BundleContext bundleContext = Activator.getDefault().getBundle().getBundleContext();
		ServiceReference<IProvisioningAgent> serviceReference = bundleContext.getServiceReference(IProvisioningAgent.class);
		IProvisioningAgent agent = bundleContext.getService(serviceReference);
		if (agent == null) {
			System.out.println(">> no agent loaded!");
			return;
		}
		// Adding the repositories
		if (! P2Util.addRepository(agent, "http://localhost/p2/repository")) {
			System.out.println(">> could no add repostory!");
			return;
		}
		// scheduling job for updates
		UpdateJob updateJob = new UpdateJob(agent);
		updateJob.schedule();
	}
}
