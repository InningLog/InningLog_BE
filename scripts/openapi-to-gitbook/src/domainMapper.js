'use strict';

const TAG_TO_DIR = {
  '직관일지': 'journal',
  '인증': 'auth',
  '좌석후기': 'seat-view',
  '커뮤니티': 'community',
  '댓글': 'comment',
  '좋아요': 'like',
  '스크랩': 'scrap',
  '회원': 'member',
  'KBO': 'kbo',
  '팀': 'team',
  '경기장': 'stadium',
  '이미지': 'image',
  '카카오': 'kakao',
  '게시글': 'post',
};

const DIR_TO_LABEL = {};
for (const [label, dir] of Object.entries(TAG_TO_DIR)) {
  DIR_TO_LABEL[dir] = label;
}

function getDir(tagName) {
  if (!tagName) return 'etc';
  return TAG_TO_DIR[tagName] || tagName.toLowerCase().replace(/\s+/g, '-');
}

function getTagLabel(dirOrTag) {
  return DIR_TO_LABEL[dirOrTag] || dirOrTag;
}

module.exports = { getDir, getTagLabel, TAG_TO_DIR };
