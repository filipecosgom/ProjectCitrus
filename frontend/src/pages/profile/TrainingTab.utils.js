export function getTotalHours(courses) {
  return courses.reduce((acc, course) => acc + (course.duration || 0), 0);
}
