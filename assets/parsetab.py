
# parsetab.py
# This file is automatically generated. Do not edit.
_tabversion = '3.2'

_lr_method = 'LALR'

_lr_signature = b'\x08u]\xcc"kT\xee\xcc\xd2\x85o:\xee\xf5\xfd'
    
_lr_action_items = {'num':([8,14,],[9,21,]),'string':([8,14,],[10,22,]),'COMMA':([9,10,21,22,23,],[14,14,14,14,14,]),'specialkeyword':([8,14,],[11,23,]),'RBRACE':([1,3,5,7,8,9,10,11,12,13,15,16,17,18,19,20,21,22,23,24,25,26,27,28,],[-1,-6,-4,-3,-1,-1,-1,-9,-10,-1,-14,-8,-7,-5,-1,-16,-1,-1,-1,28,-12,-11,-13,-15,]),'BIGLIST':([0,1,3,8,9,10,11,12,13,15,16,17,18,19,20,21,22,23,25,26,27,28,],[3,3,-6,-1,-1,-1,-9,-10,-1,-14,-8,-7,-5,3,-16,-1,-1,-1,-12,-11,-13,-15,]),'id':([0,1,3,8,9,10,11,12,13,15,16,17,18,19,20,21,22,23,25,26,27,28,],[4,4,-6,-1,-1,-1,-9,-10,-1,-14,-8,-7,-5,4,-16,-1,-1,-1,-12,-11,-13,-15,]),'$end':([0,1,2,3,5,6,7,8,9,10,11,12,13,15,16,17,18,20,21,22,23,25,26,27,28,],[-1,-1,0,-6,-4,-2,-3,-1,-1,-1,-9,-10,-1,-14,-8,-7,-5,-16,-1,-1,-1,-12,-11,-13,-15,]),'LBRACE':([8,9,10,11,12,13,15,16,17,21,22,23,25,26,27,],[-1,-1,-1,-9,-10,19,-14,-8,-7,-1,-1,-1,-12,-11,-13,]),'COLON':([4,],[8,]),}

_lr_action = { }
for _k, _v in _lr_action_items.items():
   for _x,_y in zip(_v[0],_v[1]):
      if not _x in _lr_action:  _lr_action[_x] = { }
      _lr_action[_x][_k] = _y
del _lr_action_items

_lr_goto_items = {'Definition':([0,1,19,],[1,1,1,]),'OptionalThings':([8,],[13,]),'S':([0,],[2,]),'OptionalThings2':([9,10,21,22,23,],[16,17,25,26,27,]),'OptionalBraceBlock':([13,],[18,]),'epsilon':([0,1,8,9,10,13,19,21,22,23,],[5,5,12,15,15,20,5,15,15,15,]),'DefList':([0,1,19,],[6,7,24,]),}

_lr_goto = { }
for _k, _v in _lr_goto_items.items():
   for _x,_y in zip(_v[0],_v[1]):
       if not _x in _lr_goto: _lr_goto[_x] = { }
       _lr_goto[_x][_k] = _y
del _lr_goto_items
_lr_productions = [
  ("S' -> S","S'",1,None,None,None),
  ('epsilon -> <empty>','epsilon',0,'p_epsilon','Z:\\School\\fbx2obj\\parsefbx.py',75),
  ('S -> DefList','S',1,'p_S','Z:\\School\\fbx2obj\\parsefbx.py',84),
  ('DefList -> Definition DefList','DefList',2,'p_DefList','Z:\\School\\fbx2obj\\parsefbx.py',88),
  ('DefList -> epsilon','DefList',1,'p_DefList','Z:\\School\\fbx2obj\\parsefbx.py',89),
  ('Definition -> id COLON OptionalThings OptionalBraceBlock','Definition',4,'p_Definition','Z:\\School\\fbx2obj\\parsefbx.py',102),
  ('Definition -> BIGLIST','Definition',1,'p_Definition2','Z:\\School\\fbx2obj\\parsefbx.py',112),
  ('OptionalThings -> string OptionalThings2','OptionalThings',2,'p_OptionalThings','Z:\\School\\fbx2obj\\parsefbx.py',122),
  ('OptionalThings -> num OptionalThings2','OptionalThings',2,'p_OptionalThings','Z:\\School\\fbx2obj\\parsefbx.py',123),
  ('OptionalThings -> specialkeyword','OptionalThings',1,'p_OptionalThings','Z:\\School\\fbx2obj\\parsefbx.py',124),
  ('OptionalThings -> epsilon','OptionalThings',1,'p_OptionalThings','Z:\\School\\fbx2obj\\parsefbx.py',125),
  ('OptionalThings2 -> COMMA string OptionalThings2','OptionalThings2',3,'p_OptionalThings2','Z:\\School\\fbx2obj\\parsefbx.py',134),
  ('OptionalThings2 -> COMMA num OptionalThings2','OptionalThings2',3,'p_OptionalThings2','Z:\\School\\fbx2obj\\parsefbx.py',135),
  ('OptionalThings2 -> COMMA specialkeyword OptionalThings2','OptionalThings2',3,'p_OptionalThings2','Z:\\School\\fbx2obj\\parsefbx.py',136),
  ('OptionalThings2 -> epsilon','OptionalThings2',1,'p_OptionalThings2','Z:\\School\\fbx2obj\\parsefbx.py',137),
  ('OptionalBraceBlock -> LBRACE DefList RBRACE','OptionalBraceBlock',3,'p_OptionalBraceBlock','Z:\\School\\fbx2obj\\parsefbx.py',146),
  ('OptionalBraceBlock -> epsilon','OptionalBraceBlock',1,'p_OptionalBraceBlock','Z:\\School\\fbx2obj\\parsefbx.py',147),
]
