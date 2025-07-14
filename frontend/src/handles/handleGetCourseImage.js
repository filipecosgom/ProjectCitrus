/**
 * @file handleGetCourseImage.js
 * @module handleGetCourseImage
 * @description Handles fetching the image for a course from the backend and returns a blob URL.
 * Notifies of errors and returns success status and image URL.
 * @author Project Citrus Team
 */

/**
 * Fetches the image for a course from the backend and returns a blob URL.
 * @param {string|number} courseId - ID of the course
 * @returns {Promise<Object>} Object with success, image URL, contentType, and error (if any)
 */

import { fetchCourseImage } from "../api/coursesApi";

export const handleGetCourseImage = async (courseId) => {
  try {
    const response = await fetchCourseImage(courseId);

    if (!response.success) {
      console.error("Course image fetch failed:", response.error);
      return {
        success: false,
        error: response.error || "Failed to fetch course image",
      };
    }

    const imageUrl = URL.createObjectURL(response.blob);
    return {
      success: true,
      image: imageUrl,
      contentType: response.contentType,
    };
  } catch (error) {
    console.error("Error in handleGetCourseImage:", error);
    return {
      success: false,
      error: error.message || "Unknown error fetching course image",
    };
  }
};

export default handleGetCourseImage;
