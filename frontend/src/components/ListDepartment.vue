<template>
  <div>
    <h1>Deine Feuerwehr</h1>
    <div v-if="myDepartment.id">
      <h2>{{ myDepartment.name }}</h2>
      <EditDepartment :department="myDepartment" />
    </div>

    <form v-on:submit.prevent="updateUser()">
      <v-text-field
        v-model="password"
        label="Password"
        hint="Mindestlänge 8 Zeichen"
        required
      />
      <v-text-field
        v-model="repeatPassword"
        label="Password wiederholen"
        required
      />
      <v-alert v-if="showPasswordError" type="error">
        Die Passwörter sind nicht gleich.
      </v-alert>
      <v-alert v-if="errorMessage.length > 0" type="error">
        {{ errorMessage }}
      </v-alert>
      <div></div>
      <v-container>
        <v-row justify="end">
          <v-btn
            :color="passwordSuccess ? 'success' : 'primary'"
            :loading="passwordLoading"
            type="submit"
          >
            <div v-if="passwordSuccess">
              <v-icon medium>mdi-check</v-icon> geändert
            </div>
            <div v-if="!passwordSuccess">
              Password ändern
            </div>
          </v-btn>
        </v-row>
      </v-container>
    </form>

    <div v-if="hasAdministrationRole() && departments.length > 0">
      <h2>Andere Feuerwehren</h2>
      <div v-for="department in departments" :key="department.id">
        <h3>{{ department.name }}</h3>
        <EditDepartment :department="department" />
      </div>
    </div>

    <div v-if="hasAdministrationRole()">
      <h2>Feuerwehr hinzufügen</h2>
      <AddDepartment />
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component, Watch } from "vue-property-decorator";
import EditDepartment from "./EditDepartment.vue";
import AddDepartment from "./AddDepartment.vue";

import { hasAdministrationRole } from "../services/authentication";
import {
  // eslint-disable-next-line no-unused-vars
  Department,
  getDepartments,
  getMyDepartment,
  updateDepartment
} from "../services/department";

// eslint-disable-next-line no-unused-vars
import { changePassword, User, getMe } from "../services/user";

@Component({
  components: { EditDepartment, AddDepartment }
})
export default class ListDepartment extends Vue {
  myDepartment: Department = {} as Department;
  departments: Department[] = [];
  loadingChangeDepartmentId = -1;

  password = "";
  repeatPassword = "";
  passwordLoading = false;
  passwordSuccess = false;
  user: User = {} as User;
  // TODO: Show errors
  showPasswordError = false;
  errorMessage = "";

  hasAdministrationRole = hasAdministrationRole;

  updateDepartment(departmentId: string) {
    console.log(departmentId);
    const department = [this.myDepartment, ...this.departments].find(
      dep => dep.id === departmentId
    );
    if (department) {
      updateDepartment(department);
    }
  }

  updateUser() {
    if (this.password !== this.repeatPassword) {
      this.showPasswordError = true;
      return;
    }
    this.passwordLoading = true;
    changePassword({ ...this.user, password: this.password })
      .then(() => {
        this.passwordLoading = false;
        this.passwordSuccess = true;
        this.password = "";
        this.repeatPassword = "";
        setTimeout(() => (this.passwordSuccess = false), 2000);
      })
      .catch(error => {
        this.passwordLoading = false;
        console.log(error);
        this.errorMessage = error;
      });
  }

  @Watch("password")
  onPasswordChange() {
    this.showPasswordError = false;
  }
  @Watch("repeatPassword")
  onRepeatPasswordChange() {
    this.showPasswordError = false;
  }

  mounted() {
    getMe().then(user => (this.user = user));
    getMyDepartment()
      .then(department => {
        this.myDepartment = department;
      })
      .then(() => {
        if (hasAdministrationRole()) {
          return getDepartments();
        }
      })
      .then(departments => {
        this.departments = (departments || []).filter(
          department => department.id !== this.myDepartment.id
        );
      });
  }
}
</script>

<style scoped>
.underline {
  text-decoration: underline;
}
</style>
