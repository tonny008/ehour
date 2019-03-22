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

package net.rrm.ehour.domain;

import net.rrm.ehour.util.EhourConstants;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Project Assignment type
 **/
@Entity
@Table(name = "PROJECT_ASSIGNMENT_ROLE_TYPE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProjectAssignmentRoleType extends DomainObject<Integer, ProjectAssignmentRoleType>
{
	private static final long serialVersionUID = -4306635642163206242L;

    @Id
    @Column(name = "ROLE_TYPE_ID")
	private	Integer	roleTypeId;

    @Column(name = "ROLE_TYPE", length = 64)
	private	String	roleType;
	
	public ProjectAssignmentRoleType()
	{

	}

	public ProjectAssignmentRoleType(Integer roleTypeId)
	{
		this.roleTypeId = roleTypeId;
	}


	/**
	 * @return the roleType
	 */
	public String getRoleType()
	{
		return roleType;
	}
	/**
	 * @param roleType the roleType to set
	 */
	public void setRoleType(String roleType)
	{
		this.roleType = roleType;
	}
	/**
	 * @return the roleTypeId
	 */
	public Integer getRoleTypeId()
	{
		return roleTypeId;
	}
	/**
	 * @param roleTypeId the roleTypeId to set
	 */
	public void setRoleTypeId(Integer roleTypeId)
	{
		this.roleTypeId = roleTypeId;
	}

	@Override
	public Integer getPK()
	{
		return getRoleTypeId();
	}
	/**
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(ProjectAssignmentRoleType type)
	{
		return new CompareToBuilder()
				.append(this.getRoleType(), type.getRoleType())
				.append(this.getRoleTypeId(), type.getRoleTypeId()).toComparison();
	}

	/*
	 * (non-Javadoc)
	 * @see net.rrm.ehour.domain.DomainObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other)
	{
		if ((this == other))
			return true;
		
		if (!(other instanceof ProjectAssignmentRoleType))
			return false;

		ProjectAssignmentRoleType castOther = (ProjectAssignmentRoleType) other;
		
		return new EqualsBuilder()
			.append(this.getRoleTypeId(), castOther.getRoleTypeId())
			.isEquals();
	}
	
	public int hashCode()
	{
		return new HashCodeBuilder().append(getRoleTypeId()).toHashCode();
	}
}
