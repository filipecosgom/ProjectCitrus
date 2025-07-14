/**
 * @file handleCreateNewCourse.js
 * @module handleCreateNewCourse
 * @description Handles creation of a new course and course image upload, with error mapping and notification.
 * Maps backend error codes/messages to translation keys for notification.
 * @author Project Citrus Team
 */

/**
 * Creates a new course and returns the result.
 * Maps backend errors to translation keys and notifies the user.
 * @param {Object} course - Course data object
 * @returns {Promise<Object>} Result object with success, data, error, and status
 */

/**
 * Uploads an image for a course.
 * @param {string|number} courseId - ID of the course
 * @param {File} imageFile - Image file to upload
 * @returns {Promise<Object>} Result object with success and image URL/filename if available
 */

import { createCourse, uploadCourseImage } from "../api/coursesApi";
import handleNotification from "./handleNotification";

export const handleCreateNewCourse = async (course) => {
  try {
    const response = await createCourse(course);
    if (
      response &&
      response.success === true &&
      (!response.status || (response.status >= 200 && response.status < 300))
    ) {
      return {
        success: true,
        data: response.data,
      };
    } else {
      // Map backend error codes/messages to translation keys
      let errorKey = null;
      // Use message and errorCode directly from response (due to interceptor)
      const errorMsg = response.message || response.errorCode || response.error;
      if (errorMsg) {
        if (errorMsg.includes("duplicate title"))
          errorKey = "courses.errorTitleRequired";
        else if (errorMsg.includes("duplicate link"))
          errorKey = "courses.errorLinkInvalid";
        else if (errorMsg.includes("Title is required"))
          errorKey = "courses.errorTitleRequired";
        else if (errorMsg.includes("Link is required"))
          errorKey = "courses.errorLinkRequired";
        else if (errorMsg.includes("Invalid URL"))
          errorKey = "courses.errorLinkInvalid";
        // Add more mappings as needed
      }
      handleNotification("error", errorKey || "courses.errorCourseNotCreated");
      return {
        success: false,
        error: errorKey || "courses.errorCourseNotCreated",
        status: response.status,
      };
    }
  } catch (error) {
    handleNotification("error", "courses.errorCourseNotCreated");
    return {
      success: false,
      error: error.message || "Unknown error",
      status: 500,
    };
  }
};

// Separate course image upload function
export async function handleUploadCourseImage(courseId, imageFile) {
  if (!imageFile) return { success: true };
  const response = await uploadCourseImage(courseId, imageFile);
  if (response && response.data?.success) {
    return {
      success: true,
      image: response.data.image, // Return new image URL/filename if available
    };
  }
  return { success: false, error: response?.error || "Upload failed" };
}
