import { getTotalHours } from "./TrainingTab.utils";

describe("getTotalHours", () => {
  test("retorna 0 se não houver cursos", () => {
    expect(getTotalHours([])).toBe(0);
  });

  test("soma corretamente as horas dos cursos", () => {
    const courses = [{ duration: 5 }, { duration: 10 }, { duration: 0 }];
    expect(getTotalHours(courses)).toBe(15);
  });

  test("ignora cursos sem campo duration", () => {
    const courses = [{ duration: 5 }, {}, { duration: 7 }];
    expect(getTotalHours(courses)).toBe(12);
  });
});
