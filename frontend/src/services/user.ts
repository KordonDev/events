import { Roles, withAuthenticationHeader } from "./authentication";
import { Department } from "./department";
import { putData, getData } from "../helper/fetch";

export interface User {
  id: String;
  username: String;
  password?: String;
  role?: Roles;
  department: Department;
}

export const getMe = () => getData<User>(`users/me`, withAuthenticationHeader());

export const changePassword = (user: User) =>
  putData<User>(`users/${user.id}/password`, withAuthenticationHeader(), user);
