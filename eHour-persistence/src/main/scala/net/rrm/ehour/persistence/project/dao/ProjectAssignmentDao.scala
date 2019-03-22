package net.rrm.ehour.persistence.project.dao

import java.util

import net.rrm.ehour.data.DateRange
import net.rrm.ehour.domain._
import net.rrm.ehour.persistence.dao.GenericDao

/**
 * CRUD on ProjectAssignment domain object
 **/
trait ProjectAssignmentDao extends GenericDao[Integer, ProjectAssignment] {
  /**
   * Find assigned (active) project for user
   */
  def findProjectAssignmentForUser(projectId: Integer, userId: Integer): util.List[ProjectAssignment]

  /**
   * Find project assignments for user
   */
  def findAllProjectAssignmentsForUser(user: User): util.List[ProjectAssignment]

  /**
   * Find (active) projects for user in date range
   */
  def findActiveProjectAssignmentsForUser(userId: Integer, range: DateRange): util.List[ProjectAssignment]

  /**
   * Find all projects for user in date range
   */
  def findAllProjectAssignmentsForUser(userId: Integer, range: DateRange): util.List[ProjectAssignment]

  /**
   * Find project assignments for project in range
   */
  def findProjectAssignmentsForProject(project: Project, range: DateRange): util.List[ProjectAssignment]

  /**
   * Find project assignments for customer in date range
   */
  def findProjectAssignmentsForCustomer(customer: Customer, range: DateRange): util.List[ProjectAssignment]

  /**
   * Find all project assignment types (should be in a seperate DAO theoratically)
   */
  def findProjectAssignmentTypes(): util.List[ProjectAssignmentType]

  def findProjectAssignmentRoleTypes(): util.List[ProjectAssignmentRoleType]
  /**
   * Find all project assignments for project
   */
  def findAllProjectAssignmentsForProject(project: Project): util.List[ProjectAssignment]
}


