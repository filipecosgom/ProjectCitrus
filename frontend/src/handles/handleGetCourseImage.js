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
