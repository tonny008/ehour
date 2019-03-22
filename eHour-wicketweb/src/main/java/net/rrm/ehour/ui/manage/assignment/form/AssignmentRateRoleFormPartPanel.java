/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.rrm.ehour.ui.manage.assignment.form;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.rrm.ehour.config.EhourConfig;
import net.rrm.ehour.domain.ProjectAssignmentRoleType;
import net.rrm.ehour.domain.ProjectAssignmentType;
import net.rrm.ehour.project.service.ProjectAssignmentService;
import net.rrm.ehour.ui.common.component.AjaxFormComponentFeedbackIndicator;
import net.rrm.ehour.ui.common.component.ValidatingFormComponentAjaxBehavior;
import net.rrm.ehour.ui.common.session.EhourWebSession;
import net.rrm.ehour.ui.manage.assignment.AssignmentAdminBackingBean;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.RangeValidator;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

/**
 * Rate & role
 */

public class AssignmentRateRoleFormPartPanel extends Panel {
    private static final long serialVersionUID = -3250880303705076821L;
    @SpringBean
    private ProjectAssignmentService projectAssignmentService;

    public AssignmentRateRoleFormPartPanel(String id, IModel<AssignmentAdminBackingBean> model) {
        super(id, model);

        EhourConfig config = EhourWebSession.getEhourConfig();

        // add role
        // List<String> roles = Arrays.asList("开发", "测试")
        List<ProjectAssignmentRoleType> roleTypes = projectAssignmentService.getProjectAssignmentRoleTypes();
        List<String> roles = Lists.transform(roleTypes, new Function<ProjectAssignmentRoleType, String>() {
            @Override
            public String apply(ProjectAssignmentRoleType input) {
                return input.getRoleType();
            }
        });

        final DropDownChoice<String> role = new DropDownChoice<>("projectAssignment.role", roles);
//        TextField<String> role = new TextField<>("projectAssignment.role");
        add(role);

        // add hourly rate
        TextField<Float> hourlyRate = new TextField<>("projectAssignment.hourlyRate",
                new PropertyModel<Float>(model, "projectAssignment.hourlyRate"));
        hourlyRate.setLabel(new ResourceModel("admin.assignment.rate"));
        hourlyRate.setType(Float.class);
        hourlyRate.add(new ValidatingFormComponentAjaxBehavior());
        hourlyRate.add(RangeValidator.minimum(0f));
        add(hourlyRate);
        add(new AjaxFormComponentFeedbackIndicator("rateValidationError", hourlyRate));

        // and currency
        add(new Label("currency", Currency.getInstance(config.getCurrency()).getSymbol(config.getCurrency())));
    }
}
