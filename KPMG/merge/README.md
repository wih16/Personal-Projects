## Merge

This is a plugin that contains two seperate merging options
1. Elements 
   * When two elements of the same type are selected in the tree viewer, this plugin is enabled. If selected, the user is prompted to select a "master element". The other element ("lesser element") is merged into the master element. All unique properties and relationships are merged into the master element, but the master element maintains its name and description. 
2. Models 
   * When two models are selected in the tree viewer, this plugin is enabled. If selected, the user is prompted to select a "master model". The other model ("lesser model") is merged into the master model. All elements and views present in the lesser model are added to the master model, but the master model maintains its name. For any duplicate elements, the master element in the merge is assumed to be the element from the master model. 