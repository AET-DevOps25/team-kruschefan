import { QuestionType } from '../interfaces/Question';
import { QuestionTypeCard } from '../interfaces/QuestionTypeCard';

export const QuestionTypeCards: QuestionTypeCard[] = [
  { id: '1', label: QuestionType.TEXT, imgSrc: '/assets/img/text.svg' },
  {
    id: '2',
    label: QuestionType.NUMBER,
    imgSrc: '/assets/img/number.svg',
  },
  { id: '3', label: QuestionType.DATE, imgSrc: '/assets/img/calendar.svg' },
  { id: '4', label: QuestionType.TEXT_BOX, imgSrc: '/assets/img/text_box.svg' },
  {
    id: '5',
    label: QuestionType.SINGLE_CHOICE,
    imgSrc: '/assets/img/single_choice.svg',
  },
  {
    id: '6',
    label: QuestionType.MULTIPLE_CHOICE,
    imgSrc: '/assets/img/multiple_choice.svg',
  },
  { id: '7', label: QuestionType.DROPDOWN, imgSrc: '/assets/img/dropdown.svg' },
  { id: '8', label: QuestionType.FILE, imgSrc: '/assets/img/file_upload.svg' },
  { id: '9', label: QuestionType.COMMENT, imgSrc: '/assets/img/comment.svg' },
  {
    id: '10',
    label: QuestionType.CHECKBOX,
    imgSrc: '/assets/img/checkbox.svg',
  },
];
