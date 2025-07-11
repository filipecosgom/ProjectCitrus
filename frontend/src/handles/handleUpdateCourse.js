import { updateCourse } from "../api/coursesApi";
import handleNotification from "./handleNotification";

/**
 * Handles updating a course and notifies the user of the result.
 * @param {object} updateData - The fields to update (must include id as courseId).
 * @returns {Promise<object>} The API response.
 */
export default async function handleUpdateCourse(updateData) {
  const { id: courseId, ...fields } = updateData;
  const result = await updateCourse(courseId, fields);
  if (result.success) {
    handleNotification("success", "courses.courseUpdateSuccess");
  } else {
    handleNotification("error", result.error?.message || result.error || "courseUpdateError");
  }
  return result;
}
