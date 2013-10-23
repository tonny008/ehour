package net.rrm.ehour.ui.financial.lock

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation
import net.rrm.ehour.domain.UserRole
import net.rrm.ehour.ui.common.page.AbstractBasePage
import org.apache.wicket.model.ResourceModel


@AuthorizeInstantiation(value = Array(UserRole.ROLE_REPORT))
class LockAdminPage extends AbstractBasePage[String](new ResourceModel("report.summary.title")) {

}
