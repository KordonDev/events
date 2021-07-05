<template>
  <div v-if="!error">
    <form v-on:submit.prevent="updateDepartment()">
      <v-text-field v-model="department.leaderName" label="Jugendwart" required />
      <v-text-field
        type="email"
        v-model="department.leaderEMail"
        label="Jugendwart Email"
        required
      />
      <v-container>
        <v-row justify="end">
          <v-dialog v-model="dialogOpen" persistent max-width="500">
            <template v-slot:activator="{ on, attrs }">
              <v-btn color="primary" dark v-bind="attrs" v-on="on" class="mx-3">
                Registriernugsmail versenden
              </v-btn>
            </template>
            <v-card>
              <form v-on:submit.prevent="sendRegistrationEmail">
                <v-card-title class="headline">
                  Registriernugsmail versenden
                </v-card-title>
                <v-card-text v-if="!emailSent">
                  Willst du wirklich?
                  <ol>
                    <li>Ein neues Passwort für {{ department.name }} setzen</li>
                    <li>
                      Das neue Passwort an {{ department.leaderEMail }} schicken
                    </li>
                  </ol>
                </v-card-text>
                <v-card-text v-if="emailSent">
                  <v-icon medium>mdi-check</v-icon> Registrierungsmail erfolgreich versendet
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn text @click="closeModal">
                    Schließen
                  </v-btn>
                  <v-btn color="primary" v-if="!emailSent" :loading="sendingEmail" type="submit">
                    Senden
                  </v-btn>
                </v-card-actions>
              </form>
            </v-card>
          </v-dialog>
          <v-btn color="primary" :loading="loading" type="submit">
            <span v-if="saved">
              <v-icon medium>mdi-check</v-icon> Gespeichert
            </span>
            <span v-if="!saved">
              Speichern
            </span>
          </v-btn>
        </v-row>
      </v-container>
    </form>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component, Prop } from "vue-property-decorator";

import {
  // eslint-disable-next-line no-unused-vars
  Department,
  updateDepartment
} from "../services/department";

import {
  // eslint-disable-next-line no-unused-vars
  User,
  sendRegistrationMail,
  userForDepartment
} from "../services/user";

@Component({})
export default class EditDepartment extends Vue {
  @Prop() department!: Department;
  error = false;

  loading: boolean = false;
  saved: boolean = false;

  sendingEmail: boolean = false;
  emailSent: boolean = false;
  dialogOpen: boolean = false;
  user: User = {} as User;

  closeModal() {
    this.dialogOpen = false;
    this.sendingEmail = false;
    this.emailSent = false;
  }

  sendRegistrationEmail() {
    this.sendingEmail = true;
    sendRegistrationMail(this.user.id).then(() => {
      this.emailSent = true;
      this.sendingEmail = false;
    });
  }

  updateDepartment() {
    this.loading = true;
    updateDepartment(this.department).then(() => {
      this.loading = false;
      this.saved = true;
      setTimeout(() => (this.saved = false), 2000);
    });
  }

  mounted() {
    userForDepartment(this.department.id)
      .then(user => (this.user = user))
      .catch(() => (this.error = true));
  }
}
</script>

<style scoped>
.underline {
  text-decoration: underline;
}
</style>
