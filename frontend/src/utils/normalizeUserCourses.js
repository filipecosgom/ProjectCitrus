// utils/normalizeUserCourses.js
// Utility to normalize user.completedCourses to canonical course objects for TrainingTab

export function normalizeUserCourses(completedCourses = []) {
  return completedCourses.map(c => ({
    id: c.courseId, // canonical id
    title: c.courseTitle,
    description: c.courseDescription,
    area: c.courseArea,
    duration: c.courseDuration,
    language: c.courseLanguage,
    link: c.courseLink,
    creationDate: c.courseCreationDate, // from FinishedCourseDTO
    courseHasImage: c.courseHasImage,      // match CourseCard
    courseIsActive: c.courseIsActive,      // match CourseCard
    completionDate: c.completionDate, // for filtering by year
    userId: c.userId, // optional, for reference
    userEmail: c.userEmail, // optional, for reference
  }));
}
