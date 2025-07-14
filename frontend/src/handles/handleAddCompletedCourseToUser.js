/**
 * @file handleAddCompletedCourseToUser.js
 * @module handleAddCompletedCourseToUser
 * @description Handles adding a finished course to a user and notifies the user of success or error.
 * Maps backend error codes/messages to translation keys for notification.
 * @author Project Citrus Team
 */

/**
 * Adds a finished course (or multiple courses) to a user and notifies the user.
 * Handles error mapping and notification.
 * @param {number} userId - ID of the user
 * @param {number|Array<number>} courseIds - ID(s) of the completed course(s)
 * @returns {Promise<Object>} Result object with success and errors
 */

import handleNotification from "./handleNotification";
import { addFinishedCourseToUser } from "../api/userApi";

// Add a finished course for a user and notify
export const handleAddCompletedCourseToUser = async (userId, courseIds) => {
  console.log("handleAddCompletedCourseToUser called with:", {
    userId,
    courseIds,
  }); // DEBUG
  if (!Array.isArray(courseIds)) courseIds = [courseIds];
  let allSuccess = true;
  let errors = [];
  for (const courseId of courseIds) {
    try {
      const response = await addFinishedCourseToUser(userId, courseId);
      if (
        !(
          response &&
          response.success === true &&
          (!response.status ||
            (response.status >= 200 && response.status < 300))
        )
      ) {
        // Map backend error codes/messages to translation keys
        let errorKey = null;
        const errorMsg =
          response.error || response.message || response.errorCode;
        if (errorMsg) {
          if (errorMsg.includes("already has this course as completed"))
            errorKey = "courses.errorAlreadyCompleted";
          else if (errorMsg.includes("not found"))
            errorKey = "courses.errorUserOrCourseNotFound";
        }
        errors.push(errorKey || "courses.errorCompletedCourseNotAdded");
        allSuccess = false;
      }
    } catch (error) {
      errors.push(error.message || "Unknown error");
      allSuccess = false;
    }
  }
  if (allSuccess) {
    handleNotification("success", "courses.completedCourseAdded");
    return { success: true };
  } else {
    handleNotification(
      "error",
      errors[0] || "courses.errorCompletedCourseNotAdded"
    );
    return { success: false, errors };
  }
};
