import { ISOtoString } from '@/services/ConvertDateService';

export default class Enrollment {
  id: number | null = null;
  motivation!: string;
  activityId!: number;
  enrollmentDateTime!: string;
  volunteerName!: string;
  volunteerId!: number;
  participating!: boolean;

  constructor(jsonObj?: Enrollment) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.motivation = jsonObj.motivation;
      this.activityId = jsonObj.activityId;
      this.enrollmentDateTime = ISOtoString(jsonObj.enrollmentDateTime);
      this.volunteerName = jsonObj.volunteerName;
      this.volunteerId = jsonObj.volunteerId;
      this.participating = jsonObj.participating;
    }
  }
}
