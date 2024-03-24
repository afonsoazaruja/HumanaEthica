<template>
  <v-card class="table">
    <div class="text-h3">{{ activity.name }}</div>
    <v-data-table
      :headers="headers"
      :items="enrollments"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="activityEnrollmentsTable"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <v-spacer />
          <v-btn
            color="primary"
            dark
            @click="getActivities"
            data-cy="getActivities"
            >Activities</v-btn
          >
        </v-card-title>
      </template>
      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip v-if="!item.participating && activity.hasVacancy" bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              color="primary"
              class="mr-2 action-button"
              @click="participate(item)"
              v-on="on"
              >person
            </v-icon>
          </template>
          <span>Select Participant</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <participation-selection-dialog
      v-if="currentEnrollment && editParticipationSelectionDialog"
      v-model="editParticipationSelectionDialog"
      :activity="activity"
      :enrollment="currentEnrollment"
      v-on:save-participation-selection-dialog="onSaveParticipationSelection"
      v-on:close-participation-selection-dialog="
        onCloseParticipationSelectionDialog
      "
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Enrollment from '@/models/enrollment/Enrollment';
import ParticipationSelectionDialog from '@/views/member/ParticipationSelectionDialog.vue';
import Participation from '@/models/participation/Participation';

@Component({
  components: {
    'participation-selection-dialog': ParticipationSelectionDialog,
  },
})
export default class InstitutionActivityEnrollmentsView extends Vue {
  activity!: Activity;
  enrollments: Enrollment[] = [];
  search: string = '';

  currentEnrollment: Enrollment | null = null;
  editParticipationSelectionDialog: boolean = false;

  headers: object = [
    {
      text: 'Name',
      value: 'volunteerName',
      align: 'left',
      width: '20%',
    },
    {
      text: 'Motivation',
      value: 'motivation',
      align: 'left',
      width: '50%',
    },
    {
      text: 'Participating',
      value: 'participating',
      align: 'left',
      width: '20%%',
    },
    {
      text: 'Application Date',
      value: 'enrollmentDateTime',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '5%',
    },
  ];

  async created() {
    this.activity = this.$store.getters.getActivity;
    if (this.activity !== null && this.activity.id !== null) {
      await this.$store.dispatch('loading');
      try {
        this.enrollments = await RemoteServices.getActivityEnrollments(
          this.activity.id,
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }

  async getActivities() {
    await this.$store.dispatch('setActivity', null);
    this.$router.push({ name: 'institution-activities' }).catch(() => {});
  }

  async participate(enrollment: Enrollment) {
    this.currentEnrollment = enrollment;
    this.editParticipationSelectionDialog = true;
  }

  async onCloseParticipationSelectionDialog() {
    this.currentEnrollment = null;
    this.editParticipationSelectionDialog = false;
  }

  async onSaveParticipationSelection() {
    // implement saving the participation and updating the enrollment
    this.enrollments = this.enrollments.filter(
      (a) => a.id !== this.currentEnrollment?.id,
    );

    if (this.currentEnrollment != null) {
      this.currentEnrollment.participating = true;
      this.enrollments.unshift(this.currentEnrollment);
    }
    // Close the dialog and reset currentEnrollment
    this.editParticipationSelectionDialog = false;
    this.currentEnrollment = null;
  }
}
</script>

<style lang="scss" scoped>
.date-fields-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.date-fields-row {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}
</style>
