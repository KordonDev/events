import { getData, postData, putData } from "../helper/fetch";
import { withAuthenticationHeader, getTokenData } from "./authentication";

export const getDepartments = () =>
  getData<Department[]>("departments", withAuthenticationHeader());

export const getDepartment = (id: number) =>
  getData<Department>(`departments/${id}`, withAuthenticationHeader());

export const getMyDepartment = () => {
  const departmentId = getTokenData().departmentId;
  return getDepartment(departmentId);
};

export const updateDepartment = (department: Department) =>
  putData<Department>(`departments/${department.id}`, withAuthenticationHeader(), department);

export const createDepartment = (
  name: string,
  leaderName: string,
  leaderEMail: string
) =>
  postData<Department>(`departments`, withAuthenticationHeader(), {
    name,
    leaderName,
    leaderEMail
  });

export interface Department {
  id: string;
  name: string;
  leader: string;
  leaderName: string;
  leaderEMail: string;
}
