export const validationRules = {
  email: {
    required: "Email is required.",
    pattern: {
      value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
      message: "Invalid email format.",
    },
  },
};


export function checkIfValidUsername(string) {
  const pattern = /^[a-zA-Z0-9-_]*$/;
  return pattern.test(string);
}

export function checkIfValidPassword(string) {
  const pattern = /^[a-zA-Z0-9-_+*~^]*$/;
  return pattern.test(string);
}

// Função para verificar se uma string é um número
export function checkIfNumeric(string) {
  return (
    !isNaN(string) && // Usa coerção de tipo para analisar toda a string
    !isNaN(parseFloat(string))
  ); // Garante que strings de espaços em branco falhem
}

//Função para transformar datas em array para data de javascript
export function transformArrayDatetoDate(arrayDate){
  const date = new Date(
    arrayDate[0],
    arrayDate[1], //ajustado pois o mês é como GregorianCalendar
    arrayDate[2],
    arrayDate[3],
    arrayDate[4],
    arrayDate[5]
  );
  return date;
}

//Função para transformar datas em array para data de javascript
export function transformArrayLocalDatetoLocalDate(arrayDate){
  const date = new Date(
    arrayDate[0],
    arrayDate[1] - 1,
    arrayDate[2]
  );
  return date;
}

//Função para transformar datas de javascript em string
export function dateToFormattedDate(date){
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0"); // Months are 0-based, so add 1
  const day = String(date.getDate()).padStart(2, "0");

  const formattedDate = `${year}-${month}-${day}`;
  return formattedDate;
}

//Função para transformar datas de javascript em string
export function dateToFormattedTime(date) {
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");

  return `${hours}:${minutes}`;
}